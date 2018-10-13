# deploy script for the web front-end

# This file is responsible for preprocessing all TypeScript files, making sure
# all dependencies are up-to-date, and copying all necessary files into the
# web deploy directory.

# This is the resource folder where maven expects to find our files
TARGETFOLDER=./backend/src/main/resources

# This is the folder that we used with the Spark.staticFileLocation command
WEBFOLDERNAME=web

# step 1: make sure we have someplace to put everything.  We will delete the
#         old folder tree, and then make it from scratch
rm -rf $TARGETFOLDER
mkdir $TARGETFOLDER
mkdir $TARGETFOLDER/$WEBFOLDERNAME

# there are many more steps to be done.  For now, we will just copy an HTML file
cp index.html $TARGETFOLDER/$WEBFOLDERNAME

# step 2: update our npm dependencies
npm update

# step 3: copy jQuery, Handlebars, and Bootstrap files
cp node_modules/jquery/dist/jquery.min.js $TARGETFOLDER/$WEBFOLDERNAME
cp node_modules/handlebars/dist/handlebars.min.js $TARGETFOLDER/$WEBFOLDERNAME
cp node_modules/bootstrap/dist/js/bootstrap.min.js $TARGETFOLDER/$WEBFOLDERNAME
cp node_modules/bootstrap/dist/css/bootstrap.min.css $TARGETFOLDER/$WEBFOLDERNAME
cp -R node_modules/bootstrap/dist/fonts $TARGETFOLDER/$WEBFOLDERNAME

# step 4: compile TypeScript files
node_modules/typescript/bin/tsc app.ts --strict --outFile $TARGETFOLDER/$WEBFOLDERNAME/app.js
node_modules/typescript/bin/tsc ts/MessageList.ts --strict --outFile $TARGETFOLDER/$WEBFOLDERNAME/MessageList.js
node_modules/typescript/bin/tsc ts/EditMessage.ts --strict --outFile $TARGETFOLDER/$WEBFOLDERNAME/EditMessage.js
node_modules/typescript/bin/tsc ts/Login.ts --strict --outFile $TARGETFOLDER/$WEBFOLDERNAME/Login.js
node_modules/typescript/bin/tsc ts/Profile.ts --strict --outFile $TARGETFOLDER/$WEBFOLDERNAME/Profile.js
node_modules/typescript/bin/tsc ts/NewMessage.ts --strict --outFile $TARGETFOLDER/$WEBFOLDERNAME/NewMessage.js
node_modules/typescript/bin/tsc ts/Navbar.ts --strict --outFile $TARGETFOLDER/$WEBFOLDERNAME/Navbar.js
# step 5: copy css files
cat app.css  > $TARGETFOLDER/$WEBFOLDERNAME/app.css

# step 6: compile handlebars templates to the deploy folder
#node_modules/handlebars/bin/handlebars hb/MessageList.hb >> $TARGETFOLDER/$WEBFOLDERNAME/templates.js
#node_modules/handlebars/bin/handlebars hb/EditMessage.hb >> $TARGETFOLDER/$WEBFOLDERNAME/templates.js
node_modules/handlebars/bin/handlebars hb/NewMessage.hb >> $TARGETFOLDER/$WEBFOLDERNAME/templates.js
node_modules/handlebars/bin/handlebars hb/Navbar.hb >> $TARGETFOLDER/$WEBFOLDERNAME/templates.js