SFU Market

Introduction
------------------------------------
The SFU Market application allows for students to easily search for classes and the textbooks they require, removing the need for second-hand marketplaces such as the Facebook Marketplace and Craigslist. The program is intended for users trying to save money and reduce waste by selling old textbooks instead of throwing them out. The users would either be sellers or buyers: sellers would post their products, along with the relevant information (photos, class, ISBN), while buyers would search and buy the product they require...

Installation 
------------------------------------
(For Terminal commands, copy everything within the square brackets)
- Download and Install Node: https://nodejs.org/en/ 
- Install npm express after installing Node: Run [npm install express] in the Terminal. 
- Install npm mongobd: Run [npm install mongodb] in the Terminal. 
- Install Mongobd on the local Machine: 
    - Install command-line tool first: Run [xcode-select --install]
    - Install Homebrew: Run [/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"]
        or go to the website https://brew.sh and copy the command showed in the website.
    - Install Mongobd: Go to the website https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/ and follow the instructions.
    - Check if you successfully installed Mongobd: Run [mongod] in the Terminal. If successfully, it will show some information 
        about your mongobd.

Testing
------------------------------------
- Have a valid emunator running or test on your own android device
- When testing the front-end and back-end communication: On the Terminal, locate the directory where the file "app.js" is, 
        run the command [node app.js] and the server would run, showing the message : "Listening on Port 3000..."
        Then run the app on the device. 


Maintainers
------------------------------------
- Amanda Hagara (aharaga)
- Brendon Kim (brendonk)
- Lucas Mah (lma95)
- Trevor Pinto (tpa31)
- Jonathan Yang (sya171)
 
