const express = require('express')
const app = express()
const mongoClient = require('mongodb').MongoClient

const url = "mongodb://localhost:27017"

app.use(express.json())

mongoClient.connect(url, (err, db) => {
    
    if (err) {
        console.log("Error while connecting mongo client")
    } else {
        console.log("receiving connection")
        
        const myDb = db.db('myDb')
        const collection = myDb.collection('myTable')

        app.post('/btnPost', (req, res) => {
            
            const newPost = {
                name: req.body.name,
                description: req.body.description,
                contact: req.body.contact
            }

            collection.insertOne(newPost, (err, result) => {
                res.status(200).send()
            })

        })

    }

})

app.listen(3000, () => {
    console.log("Listening on port 3000...")
})
