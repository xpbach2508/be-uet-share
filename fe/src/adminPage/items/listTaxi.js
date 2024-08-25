import React from 'react';
import '../../css/table.css';
import '../../css/radio.css';
import {URL} from "../../common";

class ListTaxi extends React.Component {

    constructor(props) {
        super(props);
        this.show = this.show.bind(this);
        this.taxiActive = this.taxiActive.bind(this);
        this.taxiNotActive = this.taxiNotActive.bind(this);
    }
    
    show(event) {
        let driverId = event.parentNode.firstChild.nextSibling.nextSibling.nextSibling.innerHTML;
        this.props.changeId(driverId);
        this.props.changeItemMenu('Show');
    }

    taxiActive() {
        const jwtToken = localStorage.getItem('jwt');
        // Create header
        let header = document.getElementsByTagName('tr')[0];
        while (header.firstChild) header.removeChild(header.firstChild);
        let thTaxiId = document.createElement('th');
        let thTaxiName = document.createElement('th');
        let thLicensePlate = document.createElement('th');
        let thDriverId = document.createElement('th');
        let thDriverName = document.createElement('th');
        let thDetail = document.createElement('th');
        thTaxiId.innerHTML = 'Taxi ID';
        thTaxiName.innerHTML = 'Taxi name';
        thLicensePlate.innerHTML = 'License plate';
        thDriverId.innerHTML = 'Driver ID';
        thDriverName.innerHTML = 'Driver name';
        thDetail.innerHTML = 'Detail';
        header.appendChild(thTaxiId);
        header.appendChild(thTaxiName);
        header.appendChild(thLicensePlate);
        header.appendChild(thDriverId);
        header.appendChild(thDriverName);
        header.appendChild(thDetail);

        let tbody = document.getElementsByTagName('tbody')[0];
        while (tbody.firstChild) tbody.removeChild(tbody.firstChild);
        let root = this;

        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (this.readyState === 4) {
                const data = JSON.parse(this.responseText);
                if (this.status === 200) {
                    for (let i = 0; i < data.length; i++) {
                        let taxi = data[i];
                        // Create tag
                        let tr = document.createElement('tr');
                        let tdCarId = document.createElement('td');
                        let tdNameTaxi = document.createElement('td');
                        let tdLicensePlate = document.createElement('td');
                        let tdDriverId = document.createElement('td');
                        let tdDriverName = document.createElement('td');
                        let tdDetail = document.createElement('td');

                        // Insert content
                        tdCarId.innerHTML = taxi.carId;
                        tdNameTaxi.innerHTML = taxi.nameCar;
                        tdLicensePlate.innerHTML = taxi.licensePlate;
                        tdDriverId.innerHTML = taxi.id;
                        tdDriverName.innerHTML = taxi.fullName;
                        tdDetail.innerHTML = 'Show';

                        // Add content
                        tr.appendChild(tdCarId);
                        tr.appendChild(tdNameTaxi);
                        tr.appendChild(tdLicensePlate);
                        tr.appendChild(tdDriverId);
                        tr.appendChild(tdDriverName);
                        tr.appendChild(tdDetail);
                        tbody.appendChild(tr);

                        // Add event onclick
                        tdDetail.style.cursor = 'pointer';
                        tdDetail.onclick = function() {
                            root.show(this);
                        }
                    }
                }
            }
        }
        xmlHttp.open('GET', URL + '/taxi/taxi_active', false);
        xmlHttp.setRequestHeader('Authorization', `Bearer ${jwtToken}`);
        xmlHttp.send(null);
    }

    taxiNotActive() {
        const jwtToken = localStorage.getItem('jwt');
        // Create header
        let header = document.getElementsByTagName('tr')[0];
        while (header.firstChild) header.removeChild(header.firstChild);
        let thTaxiId = document.createElement('th');
        let thTaxiName = document.createElement('th');
        let thLicensePlate = document.createElement('th');
        let thSeat = document.createElement('th');
        thTaxiId.innerHTML = 'Taxi ID';
        thTaxiName.innerHTML = 'Taxi name';
        thLicensePlate.innerHTML = 'License plate';
        thSeat.innerHTML = 'Capacity';
        header.appendChild(thTaxiId);
        header.appendChild(thTaxiName);
        header.appendChild(thLicensePlate);
        header.appendChild(thSeat);

        let tbody = document.getElementsByTagName('tbody')[0];
        while (tbody.firstChild) tbody.removeChild(tbody.firstChild);

        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (this.readyState === 4) {
                const data = JSON.parse(this.responseText);
                if (this.status === 200) {
                    for (let i = 0; i < data.length; i++) {
                        let taxi = data[i];
                        // Create tag
                        let tr = document.createElement('tr');
                        let tdCarId = document.createElement('td');
                        let tdNameTaxi = document.createElement('td');
                        let tdLicensePlate = document.createElement('td');
                        let tdSeat = document.createElement('td');

                        // Insert content
                        tdCarId.innerHTML = taxi.id;
                        tdNameTaxi.innerHTML = taxi.nameCar;
                        tdLicensePlate.innerHTML = taxi.licensePlate;
                        tdSeat.innerHTML = taxi.seat + ' seat';

                        // Add content
                        tr.appendChild(tdCarId);
                        tr.appendChild(tdNameTaxi);
                        tr.appendChild(tdLicensePlate);
                        tr.appendChild(tdSeat);
                        tbody.appendChild(tr);
                    }
                }
            }
        }
        xmlHttp.open('GET', URL + '/taxi/taxi_empty', false);
        xmlHttp.setRequestHeader('Authorization', `Bearer ${jwtToken}`);
        xmlHttp.send(null);
    }

    componentDidMount() {
        this.taxiActive();
    }
    
    render() {
        return(
            <div className="main-content">
                <div className="container1 mt-7">
                    <div className="row">
                        <div className="col-xl-8 m-auto order-xl-1">
                            <div className="card bg-secondary shadow">
                                <div className="card-header bg-white border-0">
                                    <div className="row align-items-center">
                                        <div className="col-8">
                                            <h3 className="mb-0">List taxi</h3>
                                            <div className="container-radio">
                                                <div className="tabs">
                                                    <input type="radio" id="radio-1" name="tabs" defaultChecked onClick={this.taxiActive}/>
                                                    <label className="tab" htmlFor="radio-1">Active</label>
                                                    <input type="radio" id="radio-2" name="tabs" onClick={this.taxiNotActive}/>
                                                    <label className="tab" htmlFor="radio-2">Inactive</label>
                                                    <span className="glider"></span>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-4 text-right">
                                            <a href="#!" className="btn btn-sm btn-primary"><i className="fas fa-car"></i></a>
                                        </div>
                                    </div>
                                </div>
                                <div className="card-body">
                                    <table className='table-taxi shadow'>
                                        <thead>
                                            <tr>
                                                <th>Taxi ID</th>
                                                <th>Taxi name</th>
                                                <th>License plate</th>
                                                <th>Driver ID</th>
                                                <th>Driver name</th>
                                                <th>Detail</th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <div className='table-container'>
                                        <table className='table-taxi shadow'>
                                            <tbody className='table-container'>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default ListTaxi