import React, { Component } from 'react';

class ChargerSetting extends Component {

   constructor(props) {
      super(props);
      this.state = {
         settings: [],
         mode: 'UNAVAILABLE',
         voltage: 0,
         ampere: 0,
         ampereDisabled: true
      };
      this.handleModeChange = this.handleModeChange.bind(this);
      this.handleAmpereChange = this.handleAmpereChange.bind(this);
   }

   componentDidMount() {
      fetch('api/settings')
         .then(response => response.json())
         .then((data) => {
            this.setState({
               settings: data,
               mode: this.getSetting(data, 'CHARGER_MODE'),
               voltage: this.getSetting(data, 'CHARGER_VOLTAGE'),
               ampere: this.getSetting(data, 'CHARGER_AMPERE_CURRENT'),
               ampereDisabled: this.isAmpereDisabled(this.getSetting(data, 'CHARGER_MODE'))
            });
         })
         .catch(console.log)
   }

   handleModeChange(event) {
      let selected = event.target.value;
      fetch('api/charger/mode', {
         method: 'PUT',
         body: selected,
         headers: {
            "Content-type": "application/json; charset=UTF-8"
         }
      }).then(response => {
         return response.json();
      }).then(json => {
         if (json) {
            console.log("successfully mode changed to: " + selected);
            this.setState({
               mode: selected,
               ampereDisabled: this.isAmpereDisabled(selected)
            });
         } else {
            console.error("ERROR mode was not changed! " + json);
         }
      });
   }

   handleAmpereChange(event) {
      let ampereNumber = event.target.value;
      fetch('api/charger/ampere', {
         method: 'PUT',
         body: ampereNumber,
         headers: {
            "Content-type": "application/json; charset=UTF-8"
         }
      }).then(response => {
         return response.json();
      }).then(json => {
         if (json) {
            console.log("successfully ampere changed to: " + ampereNumber);
            this.setState({ ampere: ampereNumber });
         } else {
            console.error("ERROR mode was not changed! " + json);
         }
      });
   }

   render() {
      return (
         <div>
            <h3>setting</h3>
            <div>mode:
						<select value={this.state.mode} onChange={this.handleModeChange}>
                  <option value="SMART">smart</option>
                  <option value="FIXED">fixed ampere</option>
                  <option value="DEACTIVATED">deactivated</option>
                  <option value="UNAVAILABLE">unavailable</option>
               </select>
            </div>
            <br />
            <div>ampere:
						<select value={this.state.ampere} onChange={this.handleAmpereChange} disabled={this.state.ampereDisabled}>
                  {this.getAmpereOptions(16)}
               </select>
            </div>
            <br />
            <div>volt: {this.state.voltage}</div>
            <br />
         </div>
      )
   }

   getSetting(data, type) {
      let found = '';
      for (const [, value] of data.entries()) {
         if (value.name == type) {
            //console.log("test## pos " + index + " value: " + value.value);
            found = value.value;
         }
      }
      if (found == '') {
         console.error("UNDEFINED setting not found for: " + type);
      }
      return found;
   }

   isAmpereDisabled(mode) {
      if (mode == 'FIXED') {
         return false;
      }
      return true;
   }

   getAmpereOptions(maxAmp) {
      let options = [];
      for (var amp = 6; amp <= maxAmp; amp++) {
         options.push(amp);
      }
      return (options.map((number) => <option key={number}>{number}</option>));
   }
}


export default ChargerSetting