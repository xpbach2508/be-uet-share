import React, { Fragment } from 'react';
import Menu from './menu';
import ItemMenu from './itemMenu';
import Home from '../homePage/home';

class Admin extends React.Component {
    
    constructor(props) {
        super(props);
        // itemMenu: để xác định UI sẽ hiển thị component nào
        this.state = {
            itemMenu: "Home"
        }
        this.changeItemMenu = this.changeItemMenu.bind(this);
    }

    changeItemMenu(type) {
        this.setState({
            itemMenu: type
        })
    }

    render() {
        if (this.state.itemMenu === 'Log out') return <Home />
        
        return(
            <Fragment>
                <ItemMenu itemMenu={this.state.itemMenu} changeItemMenu={this.changeItemMenu}/>
                <Menu changeItemMenu={this.changeItemMenu}/>
            </Fragment>
        )
    }
}

export default Admin