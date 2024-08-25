import React, { Fragment } from 'react';
import Map from './map';
import '../../../css/homeAdmin.css';
import '../../../css/upload.css';
import {URL, API_KEY} from "../../../common";

class HomeAdmin extends React.Component {

    constructor(props) {
        super(props);
        this.uploadFile = this.uploadFile.bind(this);
        this.matchGroup = this.matchGroup.bind(this);
        this.deleteAll = this.deleteAll.bind(this);
    }

    uploadFile() {
        const jwtToken = localStorage.getItem('jwt');
    }

    matchGroup() {
        const jwtToken = localStorage.getItem('jwt');
    }

    deleteAll() {
        const jwtToken = localStorage.getItem('jwt');
    }
    
    render() {
        return(
            <Fragment>
                <div className='container-map'>
                    <Map
                        googleMapURL={`https://maps.googleapis.com/maps/api/js?key=${API_KEY}&callback=initMap`}
                        loadingElement={<div style={{ height: `100%` }} />}
                        containerElement={<div style={{ height: `92vh`, margin: `auto`, border: '0px solid black' }} />}
                        mapElement={<div style={{ height: `100%` }} />}
                    />
                </div>
                {/* <div className='up-excel shadow'>
                    <h3 className="mb-0">Ghép nhóm</h3>
                    <button className="cssbuttons-io-button" onClick={this.uploadFile}>
                        <svg viewBox="0 0 640 512" fill="white" height="1em" xmlns="http://www.w3.org/2000/svg"><path d="M144 480C64.5 480 0 415.5 0 336c0-62.8 40.2-116.2 96.2-135.9c-.1-2.7-.2-5.4-.2-8.1c0-88.4 71.6-160 160-160c59.3 0 111 32.2 138.7 80.2C409.9 102 428.3 96 448 96c53 0 96 43 96 96c0 12.2-2.3 23.8-6.4 34.6C596 238.4 640 290.1 640 352c0 70.7-57.3 128-128 128H144zm79-217c-9.4 9.4-9.4 24.6 0 33.9s24.6 9.4 33.9 0l39-39V392c0 13.3 10.7 24 24 24s24-10.7 24-24V257.9l39 39c9.4 9.4 24.6 9.4 33.9 0s9.4-24.6 0-33.9l-80-80c-9.4-9.4-24.6-9.4-33.9 0l-80 80z"></path></svg>
                        <span>Upload</span>
                    </button>
                    <button className="cssbuttons-io-button" onClick={this.matchGroup}>
                        <span>Ghép nhóm</span>
                    </button>
                    <h3 className="mb-0 delete" onClick={this.deleteAll}>Xóa tất cả tuyến thường xuyên</h3>
                    <button className="cssbuttons-io-button">
                        <span>Xóa tất cả</span>
                    </button>
                </div> */}
            </Fragment>
        )
    }
}

export default HomeAdmin