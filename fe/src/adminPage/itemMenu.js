import React from 'react';
import HomeAdmin from './items/homePage/homeAdmin';
import ListTaxi from './items/listTaxi';
import CreateTaxi from './items/createTaxi';
import CreateDriver from './items/createDriver';
import Account from './items/account';
import TaxiDetail from './items/taxiDetail';

class ItemMenu extends React.Component {

    constructor(props) {
        super(props);
        // itemMenu: để xác định UI sẽ hiển thị component nào
        this.state = {
            driverId: null
        }
        this.changeId = this.changeId.bind(this);
    }

    changeId(driverId) {
        this.setState({
            driverId: driverId
        })
    }

    render() {
        // Dựa vào interfaceType để hiện UI phù hợp
        switch(this.props.itemMenu) {
            case "List taxi": return <ListTaxi changeItemMenu={this.props.changeItemMenu} changeId={this.changeId}/>
            case "Create taxi": return <CreateTaxi />
            case "Create driver": return <CreateDriver />
            case "Account": return <Account />
            case "Show": return <TaxiDetail changeItemMenu={this.props.changeItemMenu} driverId={this.state.driverId}/>
            default: return <HomeAdmin />
        }
    }
}

export default ItemMenu