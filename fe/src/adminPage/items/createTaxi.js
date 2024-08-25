import React from "react";
import '../../css/account.css';
import '../../css/buttonCreateTaxi.css';
import {URL} from "../../common";

class CreateTaxi extends React.Component {

    constructor(props) {
        super(props);
        this.createTaxi = this.createTaxi.bind(this);
    }

    createTaxi(event) {
        event.preventDefault();
        const jwtToken = localStorage.getItem('jwt');
        let licensePlate = document.getElementById('license-plate').value;
        let seat = document.getElementById('seat').value;
        let nameCar = document.getElementById('name-car').value;
        let error = document.getElementsByClassName('errCreate')[0]; // span hiển thị lỗi
        error.innerHTML = '';
        if (!licensePlate || !seat || !nameCar) {
            error.innerHTML = 'Please fill in complete information';
            return;
        }

        const xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (this.readyState === 4) {
                if (this.status === 200) {
                    alert('Success!');
                } else error.innerHTML = this.response;
            }
        }
        xmlHttp.open('POST', URL + '/taxi/create', false);
        xmlHttp.setRequestHeader("Content-type", "application/json");
        xmlHttp.setRequestHeader('Authorization', `Bearer ${jwtToken}`);
        const requestData = {
            licensePlate: licensePlate,
            seat: seat,
            nameCar: nameCar
        }
        xmlHttp.send(JSON.stringify(requestData));
    }

    render() {
        return (
            <div className="main-content">
                <div className="container mt-7">
                    <div className="row">
                        <div className="col-xl-8 m-auto order-xl-1">
                            <div className="card bg-secondary shadow">
                                <div className="card-header bg-white border-0">
                                    <div className="row align-items-center">
                                        <div className="col-8">
                                            <h3 className="mb-0">Create taxi</h3>
                                        </div>
                                        <div className="col-4 text-right">
                                            <a href="#!" className="btn btn-sm btn-primary"><i className="fas fa-car"></i></a>
                                        </div>
                                    </div>
                                </div>
                                <div className="card-body">
                                    <form onSubmit={this.createTaxi}>
                                        <div className="pl-lg-4">
                                            <div className="row">
                                                <div className="col-lg-6">
                                                    <div className="form-group focused">
                                                        <label className="form-control-label" htmlFor="license-plate">license plate</label>
                                                        <input type="text" id="license-plate" className="form-control form-control-alternative" />
                                                    </div>
                                                </div>
                                                <div className="col-lg-6 col-lg-6-child-2">
                                                    <div className="form-group">
                                                        <label className="form-control-label" htmlFor="seat">Capacity</label>
                                                        <input type="text" id="seat" className="form-control form-control-alternative" />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="pl-lg-4">
                                            <div className="row">
                                                <div className="col-md-12">
                                                    <div className="form-group focused">
                                                        <label className="form-control-label" htmlFor="name-car">Taxi name</label>
                                                        <input id="name-car" className="form-control form-control-alternative" type="text" />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <span className='errCreate'></span>
                                        <button className="create-taxi" type="submit">
                                            <span className="text">Create</span>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default CreateTaxi;
