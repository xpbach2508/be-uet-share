import React from 'react';
import { withGoogleMap, withScriptjs, GoogleMap, Marker, DirectionsRenderer } from "react-google-maps";
import InfoBox from 'react-google-maps/lib/components/addons/InfoBox';
import {URL} from "../../../common";

const options = { closeBoxURL: '', enableEventPropagation: true };
const colors = ["#FF0000", "#00cc00", "#ff66ff", "#0000ff", "#00000", "#ffff00", "#00ffff", "#993399"];

class Map extends React.Component {

  constructor(props) {
    super(props);
    /*
      markers: list các điểm start of taxi để insert InfoBox vào info of driver
      schedules: array 2 chiều, each element is schedule of a driver
    */
    this.state = {
      markers: [],
      directions: [],
      groupIdDirections: [],
      markersFullNoStart: [],
      selectedMarker: true
    };
    this.createDirection = this.createDirection.bind(this);
  }

  createDirection(root, listPointSchedule) {
    let arrDirections = [];
    for (let i = 0; i < listPointSchedule.length; i++) {
      //create request
      var request = listPointSchedule[i];

      var directionsService = new window.google.maps.DirectionsService();
      //pass the request to the route method
      directionsService.route(request, function (result, status) {
        if (status === window.google.maps.DirectionsStatus.OK) {
          arrDirections[i] = result;
          root.setState({
            directions: arrDirections
          });
        } else {
          console.error(`error fetching directions ${result}`);
        }
      });
    }
  }

  // Show all schedule of all taxi
  componentDidMount() {
    const jwtToken = localStorage.getItem('jwt');
    let root = this;
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (this.readyState === 4) {
            if (this.status === 200) {
              const data = JSON.parse(this.responseText);
              let listPointMarker = [], index = 0;
              let listPointSchedule = [], listColorDirection = [], index2 = 0;
              let listPointMarkerNoPointStart = [], index3 = 0;
              let len = data.length;
              for (let i = 0; i < len; i++) {
                if (i === 0) {
                  // marker
                  listPointMarker[index] = {
                    lat: data[i].lat,
                    lng: data[i].lng,
                    driverName: data[i].driverName,
                    licensePlate: data[i].licensePlate,
                    nameCar: data[i].nameCar,
                    expectedTimeString: data[i].expectedTimeString
                  };
                  index++;
                }
                else if (data[i].groupId === data[i - 1].groupId) {
                  // schedule
                  listPointSchedule[index2] = {
                    origin: new window.google.maps.LatLng(data[i - 1].lat, data[i - 1].lng),
                    destination: new window.google.maps.LatLng(data[i].lat, data[i].lng),
                    travelMode: 'DRIVING'
                  };
                  listColorDirection[index2] = data[i].groupId;
                  index2++;

                  // markersFullNoStart
                  listPointMarkerNoPointStart[index3] = {
                    lat: data[i].lat,
                    lng: data[i].lng,
                    expectedTimeString: data[i].expectedTimeString
                  };
                  index3++;
                } else {
                  // marker
                  listPointMarker[index] = {
                    lat: data[i].lat,
                    lng: data[i].lng,
                    driverName: data[i].driverName,
                    licensePlate: data[i].licensePlate,
                    nameCar: data[i].nameCar,
                    expectedTimeString: data[i].expectedTimeString
                  };
                  index++;
                }
              }

              root.setState({
                markers: listPointMarker,
                markersFullNoStart: listPointMarkerNoPointStart,
                groupIdDirections: listColorDirection
              })
              root.createDirection(root, listPointSchedule);
            }
        }
    }
    xmlHttp.open('GET', URL + '/schedule/admin', false);
    xmlHttp.setRequestHeader('Authorization', `Bearer ${jwtToken}`);
    xmlHttp.send(null);
  }

  render() {
    return (
      <div>
        <GoogleMap defaultZoom={1} defaultCenter={{ lat: 21.028511, lng: 105.804817 }}>
            {
              this.state.markers && this.state.markers.map((position, index) => 
                <Marker
                  position={new window.google.maps.LatLng(position)}
                  icon={{
                    url: 'http://maps.google.com/mapfiles/kml/pal4/icon7.png'
                  }}
                  onClick={() => {
                    if (this.state.selectedMarker === true) {
                      this.setState({ selectedMarker: false });
                    } else {
                      this.setState({ selectedMarker: true });
                    }
                  }}
                > 
                  {this.state.selectedMarker === true && (
                    <InfoBox options={options}>
                      <>
                        <div style={{ backgroundColor: 'green', color: 'white', borderRadius: '1em', padding: '0.2em', width: '100px', fontSize: '12px', textAlign: 'center' }}>
                          {"Expected: " + position.expectedTimeString} <br />
                          {position.driverName} <br />
                          {position.licensePlate} <br />
                          {position.nameCar}
                        </div>
                      </>
                    </InfoBox>
                  )}   
                </Marker>
              )
            }
            {
              this.state.markersFullNoStart && this.state.markersFullNoStart.map((position, index) => 
                <Marker position={new window.google.maps.LatLng(position)}> 
                  <InfoBox options={options}>
                    <>
                        <div style={{ backgroundColor: 'green', color: 'white', borderRadius: '1em', padding: '0.2em', width: '100px', fontSize: '12px', textAlign: 'center' }}>
                          {"Expected: " + position.expectedTimeString}
                        </div>
                    </>
                  </InfoBox>   
                </Marker>
              )
            }
            {
              this.state.directions && this.state.directions.map((direction, index) => 
                <DirectionsRenderer
                  directions={direction}
                  options={{
                    polylineOptions: {
                      strokeColor: colors[this.state.groupIdDirections[index] % colors.length]
                    },
                    suppressMarkers: true
                  }}
                />
              )
            }
        </GoogleMap>
      </div>
    );
  }

}

export default withScriptjs(withGoogleMap(Map));

