SFU Market

Preface
------------------------------------
All of us are completely new to every tool we are using this far, so we have been performing numerous spikes to catch up in knowledge about what tools we are using and from there, determine how much time we need to do such tasks. As a result of that, we have run into some trouble with the current database solution, MongoDB with NodeJS, and will be changing to mySQL with PHP for various reasons. This includes: easier access to documentation, better suits our needs, and ability to connect to an apache web server via Xampp for testing. This has created technical debt for us. Another result of this is that we have not been using the merging or issues feature on the SFU git lab and will begin utilizing these features for the following sprints.

Introduction
------------------------------------
The SFU Market application allows for students to easily search for classes and the textbooks they require, removing the need for second-hand marketplaces such as the Facebook Marketplace and Craigslist. The program is intended for users trying to save money and reduce waste by selling old textbooks instead of throwing them out. The users would either be sellers or buyers: sellers would post their products, along with the relevant information (photos, class, ISBN), while buyers would search and buy the product they require...

Installation 
------------------------------------
(For Terminal commands, copy everything within the square brackets)
- Download and Install **Node**: https://nodejs.org/en/ 
- Install **npm express** after installing Node: Run [npm install express] in the Terminal. 
- Install **npm mongobd**: Run [npm install mongodb] in the Terminal. 
- Install **Mongobd** on the local Machine: 
    - Install **command-line tool** first: Run [xcode-select --install]
    - Install **Homebrew**: Run [/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"]
        or go to the website https://brew.sh and copy the command showed in the website.
    - Install **Mongobd**: Go to the website https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/ and follow the instructions.
    - Check if you successfully installed Mongobd: Run [mongod] in the Terminal. If successfully, it will show some information 
        about your mongobd.

Testing
------------------------------------
- Have a valid **emulator** running or test on **your own android device**
- When testing the front-end and back-end communication: On the Terminal, locate the directory where the file "app.js" is, 
        run the command [node app.js] and the server would run, showing the message : "Listening on Port 3000..."
        Then run the app on the device. 

(currently, the image uploaded does not properly store, one of the reasons for the change to mySQL and the addition of Apache Web server)


Maintainers
------------------------------------
- Amanda Hagara (aharaga)
- Brendon Kim (brendonk)
- Lucas Mah (lma95)
- Trevor Pinto (tpa31)
- Jonathan Yang (sya171)
 
