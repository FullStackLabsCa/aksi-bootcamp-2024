### _Phase 1:_

**Input**: Single Trade File

**Processing**: Read a file and make 10 Chunks out of it
    Single Threaded

**Output**: 10 smaller Trade Files

---
### _Phase 2:_

**Input**: Location of 10 Chunks

**Processing**:

    NOTE: Multithreading and 
    Each thread processes 1 chunk and does the following tasks:
    
        Get Data from the Chunk
        Extract Trade_Id, Account Number from the data
        Validate if the Trade is valid based on the number of entries
    
        Write the payload to the raw database with
            trade_id
            status (valid/invalid)
            payload
    
        If status of the data is valid
            look up the concurrent map for queue mapping (getQueueMapping) based on the account number
            put the trade_id in the queue as determined by above

**Output**: 3 Queues will be filled up with trade_id's

---
### _Phase 3:_

**Input**: 3 Queues

**Processing**:

    Thread pool of 3 threads will launch.

    1 thread will work on each queue and does the following work:
        {
            Get the payload of the trade_id from the raw database
            
            Does Data Validation and Create Trade Object from the Payload
            
            If data validation sucess and creation of trade object success:
                Perform Business Validation on the Trade (securities Look up)    
            
            If Business Validation Success:
                Insert into the journal database
        } 
        Till here if anything fails:        
            - Put the trade_ID back in the queue
            - Keep a countdown latch of 3 times
            - If it fails 3 times, add to DLQ

        Once Insertion into Journal Database is success
            Update the Positions table


**Output**: Data Inserted into the Journal Database and the Positions Database

---
### _Phase 4:_

**Input:** NaN

**Processing:**
    
    Poll the Journal Database and the Positions database for Analytics

    CommandLine

    Overwrite the current state of the commandline, do not print new state

**Output:**


---
### _Phase X:_

**Input:**

**Processing:**

**Output:**
