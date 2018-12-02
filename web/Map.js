var map;
src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAmDqOtHJ-c9NITDnC_VrsMrbcKk-NI4k4&callback=initMap";
function initMap() {
  map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: -34.397, lng: 150.644},
    zoom: 8
  });
}