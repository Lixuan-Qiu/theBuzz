var map;
var marker = null;
function initMap() {
  console.log("initmap");
  map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 40.607164238, lng: -75.378998484},
    zoom: 6
  });
  // This event listener calls addMarker() when the map is clicked.
  map.addListener('click', function(e) {
  if(marker != null){
    marker.setMap(null);
  }
  marker = new google.maps.Marker({
    position: e.latLng,
    map: map
  });
  latitude = e.latLng.lat();
  longtitude = e.latLng.lng();  
  console.log("Lat: "+latitude);
  console.log("Lat type: "+ typeof latitude);
  map.panTo(e.latLng);
});
}

