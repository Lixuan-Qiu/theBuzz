

/**
 * mMap encapsulates all of the code for the form for adding an entry
 */
declare function changeLocation(): void;
class mMap {
    
        /**
         * The name of the DOM entry associated with mMap
         */
        public static readonly NAME = "Map";
        /**
         * Track if the Singleton has been initialized
         */
        private static isInit = false;
    
        private static id = -1;
    
        /**
         * Initialize the mMap by creating its element in the DOM and
         * configuring its buttons.  This needs to be called from any public static 
         * method, to ensure that the Singleton is initialized before use
         */
        private static init() {
            if (!mMap.isInit) {
                //$("body").append(Handlebars.templates[mMap.NAME + ".hb"]());
                $("#" + mMap.NAME + "-OK").click(mMap.hide);
                $("#" + mMap.NAME + "-Close").click(mMap.cancel);
                mMap.isInit = true;
            }
        }
    
        /**
         * Refresh() doesn't really have much meaning, but just like in sNavbar, we
         * have a refresh() method so that we don't have front-end code calling
         * init().
         */
        public static refresh() {
            mMap.init();
        }
    
        /**
         * Hide the mMap.  Be sure to clear its fields first
         */
        private static hide() {
            $("#" + mMap.NAME + "-message").val("");
            $("#" + mMap.NAME).modal("hide");
        }
    
        /**
         * Show the NewEntryForm.  Be sure to clear its fields, because there are
         * ways of making a Bootstrap modal disapper without clicking Close, and
         * we haven't set up the hooks to clear the fields on the events associated
         * with those ways of making the modal disappear.
         */
        public static show(data: any) {
            if(!(data.mData.latitude === 360.0)){
                LAT_VALUE = Number(data.mData.latitude);
                LONG_VALUE = Number(data.mData.longitude);
            }
            changeLocation();
            $("#" + mMap.NAME).modal("show");
            console.log("mMap show");
        }
        
        public static show_2() {
            $("#" + mMap.NAME).modal("show");
            console.log("mMap show_2");
        }

    
        /**
         * cancel the location marker
         */
        private static cancel() { 
            // hide the modal
            latitude = 360.0;
            longtitude = 360.0
            mMap.hide();
        }
    }