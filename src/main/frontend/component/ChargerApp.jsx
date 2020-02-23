import React, { Component } from 'react';
import ChargerSetting from "./ChargerSetting.jsx";
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

class ChargerApp extends Component {

   constructor(props) {
      super(props);
      this.state = { charger: [] };
   }

   componentDidMount() {
      fetch('api/chargerstatus')
         .then(response => response.json())
         .then((data) => {
            this.setState({ charger: data })
         })
         .catch(console.log)
   }

   render() {
      return (
         <div className='boxed'>
            <Row>
               <Col>
                  <img src='images/charger.png' width='60px' />
                  <div>{this.state.charger.temperature} °C</div>
                  <div>{this.state.charger.ampere} A</div>
               </Col>
               <Col>
                  <h3>charger</h3>
                  <div>connection: {this.state.charger.connectionStatus}</div>
                  <br />
                  <div>charging: {String(this.state.charger.allowCharging)}</div>
                  <div>total: {(this.state.charger.loadedMkWhTotal / 10)} kWh</div>
                  <div>autostop: {String(this.state.charger.autoStop)}</div>
                  <div>stop at: {this.state.charger.autoStopMkWh} kWh</div>
               </Col>
               <Col>
                  <ChargerSetting />
               </Col>
            </Row>
         </div>
      )
   }
}

export default ChargerApp;