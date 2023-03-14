package com.poc.mongodb.db.service

import com.azure.identity.AzureAuthorityHosts
import com.azure.identity.DefaultAzureCredential
import com.azure.identity.DefaultAzureCredentialBuilder
import com.azure.messaging.eventhubs.EventData
import com.azure.messaging.eventhubs.EventHubClientBuilder
import com.azure.messaging.eventhubs.EventHubProducerClient
import com.azure.messaging.eventhubs.EventProcessorClientBuilder
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore
import com.azure.messaging.eventhubs.models.ErrorContext
import com.azure.messaging.eventhubs.models.EventContext
import com.azure.messaging.eventhubs.models.PartitionContext
import com.azure.storage.blob.BlobContainerClientBuilder
import com.poc.mongodb.db.model.InventoryItem
import com.poc.mongodb.db.model.InventoryLevel
import com.poc.mongodb.db.model.OrgInventoryItems
import com.poc.mongodb.db.repository.InventoryItemRepository
import com.poc.mongodb.db.repository.InventoryItemsRepository
import com.poc.mongodb.db.repository.InventoryLevelRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer
import com.fasterxml.jackson.databind.ObjectMapper

@Service
class InventoryItemsService(
    val inventoryItemsRepository: InventoryItemsRepository,
    val inventoryItemRepository: InventoryItemRepository,
    val inventoryLevelRepository: InventoryLevelRepository,
    @Autowired val objectMapper: ObjectMapper
) {

    private val namespaceName = "sparkpoc.servicebus.windows.net"

    private val eventHubName = "inventoryitems"
    fun addInventoryItem(orgInventoryItem: OrgInventoryItems):OrgInventoryItems {

        orgInventoryItem.inventoryItems.forEach { f ->

            val itemPrev = inventoryItemRepository.findBySkuNOrg(f.sku,orgInventoryItem.org.id.toString())

            val item = itemPrev.isEmpty?.let {
                inventoryItemRepository.save(
                    InventoryItem(
                        sku = f.sku,
                        effectiveTs = f.effectiveTs,
                        orgid = orgInventoryItem.org.id.toString(),
                    )
                )
            }

            f.inventoryLevels.forEach { l->
                inventoryLevelRepository.save(
                    InventoryLevel(
                        count = l.count,
                        locationId = l.locationId,
                        orgSku = item!!
                    )
                )
            }
        }

        return orgInventoryItem
    }

    fun updateInventoryItem(orgInventoryItem: OrgInventoryItems) {
        var savedInventoryItem:OrgInventoryItems
        = orgInventoryItem.org.id?.let {
            inventoryItemsRepository.findByOrgId(it)
                .orElseThrow { throw RuntimeException("Cannot find inventory item by org id") }
        }!!
//        savedInventoryItem.id = orgInventoryItem.id
//        savedInventoryItem.org.id = orgInventoryItem.org.id
        savedInventoryItem.org.id = "TESTAD001"
        inventoryItemsRepository.save(savedInventoryItem)
    }

    fun getAllInventoryItems() : List<OrgInventoryItems> = inventoryItemsRepository.findAll()

    fun getInventoryItemBySku(sku:String):List<OrgInventoryItems> =inventoryItemsRepository.findBySku(sku).orElseThrow{ throw RuntimeException("Cannot find Inventory by Sku") }

    fun getInventoryItemByOrgId(org_id:String):OrgInventoryItems =inventoryItemsRepository.findByOrgId(org_id).orElseThrow{ throw RuntimeException("Cannot find Inventory by Org id") }

    fun deleteInventoryItem(id:String)=inventoryItemsRepository.deleteByOrgId(id)

    fun publishEvents(orgInventoryItem: OrgInventoryItems) {
        // create a token using the default Azure credential
        val credential: DefaultAzureCredential = DefaultAzureCredentialBuilder()
            .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
            .build()

        // create a producer client
        val producer: EventHubProducerClient = EventHubClientBuilder()
            .fullyQualifiedNamespace(namespaceName)
            .eventHubName(eventHubName)
            .credential(credential)
            .buildProducerClient()

        val inventoryPayload: String = objectMapper.writeValueAsString(orgInventoryItem)
        val payloadEvent = EventData(inventoryPayload.toByteArray(StandardCharsets.UTF_8))
        // sample events in an array
        val allEvents: List<EventData> = Arrays.asList(payloadEvent)

        // create a batch
        var eventDataBatch = producer.createBatch()
        for (eventData: EventData? in allEvents) {
            // try to add the event from the array to the batch
            if (!eventDataBatch.tryAdd(eventData)) {
                // if the batch is full, send it and then create a new batch
                producer.send(eventDataBatch)
                eventDataBatch = producer.createBatch()

                // Try to add that event that couldn't fit before.
                if (!eventDataBatch.tryAdd(eventData)) {
                    throw IllegalArgumentException(
                        "Event is too large for an empty batch. Max size: "
                                + eventDataBatch.maxSizeInBytes
                    )
                }
            }
        }
        // send the last batch of remaining events
        if (eventDataBatch.count > 0) {
            producer.send(eventDataBatch)
        }
        producer.close()
    }

    fun consumeEvents() {

        // create a token using the default Azure credential
        val credential = DefaultAzureCredentialBuilder()
            .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
            .build()
        // Create a blob container client that you use later to build an event processor client to receive and process events
        val blobContainerAsyncClient = BlobContainerClientBuilder()
            .credential(credential)
            .endpoint("https://sparkinventorycheckpoint.blob.core.windows.net")
            .containerName("track")
            .buildAsyncClient()
        // Create an event processor client to receive and process events and errors.
        val eventProcessorClient = EventProcessorClientBuilder()
            .fullyQualifiedNamespace(namespaceName)
            .eventHubName(eventHubName)
            .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
            .processEvent(PARTITION_PROCESSOR)
            .processError(ERROR_HANDLER)
            .checkpointStore(BlobCheckpointStore(blobContainerAsyncClient))
            .credential(credential)
            .buildEventProcessorClient()

        println("Starting event processor")
        eventProcessorClient.start()

        println("Press enter to stop.")
        System.`in`.read()

        println("Stopping event processor")
        eventProcessorClient.stop()
        println("Event processor stopped.")

        println("Exiting process")

    }

}

val PARTITION_PROCESSOR: Consumer<EventContext> = Consumer<EventContext> { eventContext ->
    val partitionContext: PartitionContext = eventContext.getPartitionContext()
    val eventData: EventData = eventContext.getEventData()
    System.out.printf(
        "Processing event from partition %s with sequence number %d with body: %s%n",
        partitionContext.partitionId, eventData.sequenceNumber, eventData.bodyAsString
    )

    // Every 10 events received, it will update the checkpoint stored in Azure Blob Storage.
    if (eventData.sequenceNumber % 10 == 0L) {
        eventContext.updateCheckpoint()
    }
}

val ERROR_HANDLER: Consumer<ErrorContext> = Consumer<ErrorContext> { errorContext ->
    System.out.printf(
        "Error occurred in partition processor for partition %s, %s.%n",
        errorContext.getPartitionContext().getPartitionId(),
        errorContext.getThrowable()
    )
}