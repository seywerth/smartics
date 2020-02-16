import React, { Component } from 'react';
import ChargerSetting from "./ChargerSetting.jsx";

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
         <div>
            <table>
               <thead></thead>
               <tbody>
                  <tr>
                     <td>
                        <img src='images/charger.png' width='60px' />
                     </td>
                     <td>
                        <h3>charger</h3>
                        <div>connection: {this.state.charger.connectionStatus}</div>
                        <br />
                        <div>charging: {String(this.state.charger.allowCharging)}</div>
                        <div>total: {(this.state.charger.loadedMkWhTotal / 10)} kWh</div>
                     </td>
                     <td rowSpan='2'>
                        <ChargerSetting />
                     </td>
                  </tr>
                  <tr>
                     <td>
                        <div>{this.state.charger.temperature} °C</div>
                        <div>{this.state.charger.ampere} A</div>
                     </td>
                     <td>
                        <div>autostop: {String(this.state.charger.autoStop)}</div>
                        <div>stop at: {this.state.charger.autoStopMkWh} kWh</div>
                     </td>
                  </tr>
               </tbody>
            </table>
         </div>
      )
   }
}

export default ChargerApp
