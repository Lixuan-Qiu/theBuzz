function changeLocation() {
    var myLatLng = new google.maps.LatLng(LAT_VALUE, LONG_VALUE);
    console.log("myLatLng: "+myLatLng);
  map.panTo(myLatLng);
}