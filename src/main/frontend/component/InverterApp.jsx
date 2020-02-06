import React, { Component } from 'react';
import EnergyChart from "./EnergyChart.jsx";

class InverterApp extends Component {

   constructor(props) {
      super(props);
      this.handleNavPrev = this.handleNavPrev.bind(this);
      this.handleNavNext = this.handleNavNext.bind(this);
      this.state = {
         inverter: [],
         selectDate: new Date(),
         prevDate: this.selectPreviousDay(new Date()),
         nextDate: this.selectNextDay(new Date())
      };
   }

   componentDidMount() {
      this.onNavigate(this.state.selectDate);
   }

   onNavigate(selectDate) {
      fetch('api/meterdatasummary/' + this.getCurrentDate(selectDate))
         .then(response => response.json())
         .then((data) => {
            this.setState({
               inverter: data,
               selectDate: selectDate,
               prevDate: this.selectPreviousDay(selectDate),
               nextDate: this.selectNextDay(selectDate)
            })
         })
         .catch(console.log)
   }

   handleNavPrev(e) {
      e.preventDefault();
      console.log("handleNavPrev: " + this.state.prevDate);
      this.onNavigate(this.state.prevDate);
   }

   handleNavNext(e) {
      e.preventDefault();
      console.log("handleNavNext: " + this.state.nextDate);
      this.onNavigate(this.state.nextDate);
   }

   render() {
      return (
         <div>
            <table>
               <thead></thead>
               <tbody>
                  <tr>
                     <td><img src='images/inverter.png' width='110px' /></td>
                     <td>
                        <h2>inverter {this.getCurrentDate(this.state.selectDate)}</h2>
                        <div>status: {this.state.inverter.status}</div>
                     </td>
                  </tr>
                  <tr>
                     <td>
                        <div>produced: {this.state.inverter.powerProduced}</div>
                        <div>feedback: {this.state.inverter.powerFeedback}</div>
                        <div>autonomy: {this.state.inverter.autonomy}</div>
                     </td>
                     <td>
                        <div>consumed: {this.state.inverter.powerConsumed}</div>
                        <div>from network: {this.state.inverter.powerFromNetwork}</div>
                        <div>from production: {this.state.inverter.powerFromProduction}</div>
                     </td>
                  </tr>
                  <tr>
                     <td colSpan='2'>
                        <EnergyChart data={this.getEnergyForMins(this.state.inverter.meteringDataMinDtos)} size={[550, 300]}
                           showline={this.getCurrentDate(new Date()) == this.getCurrentDate(this.state.selectDate)} />
                     </td>
                  </tr>
                  <tr>
                     <td>
                        <button key="prev" onClick={this.handleNavPrev}>&lt; previous day</button> &nbsp;
                        {this.prettyDate(this.state.inverter.fromTime)}
                     </td>
                     <td>
                        {this.prettyDate(this.state.inverter.untilTime)} &nbsp;
                        <button key="next" onClick={this.handleNavNext}>next day &gt;</button>
                     </td>
                  </tr>
               </tbody>
            </table>
         </div >
      );
   }

   selectPreviousDay(selectDate) {
      if (selectDate == undefined) {
         return;
      }
      const day = selectDate.getDate();
      let newDate = new Date(selectDate);
      newDate.setDate(day - 1);
      return newDate;
   }

   selectNextDay(selectDate) {
      if (selectDate == undefined) {
         return;
      }
      const day = selectDate.getDate();
      let newDate = new Date(selectDate);
      newDate.setDate(day + 1);
      return newDate;
   }

   getEnergyForMins(dto) {
      if (dto == undefined) {
         return [];
      }
      // multiply 5min by 12 to get Wh
      return dto.map(item => [this.prettyTime(item.startTime), item.powerConsumed * 12, item.powerProduced * 12]);
   }

   prettyTime(jsonDate) {
      let date = new Date();
      date.setTime(Date.parse(jsonDate));
      return Math.round(date.getHours() * 12 + date.getMinutes() / 5);
   }

   prettyDate(jsonDate) {
      let date = new Date();
      if (jsonDate == undefined) {
         return date.toLocaleString();
      }
      date.setTime(Date.parse(jsonDate));
      return new Intl.DateTimeFormat("at-DE", {
         year: "numeric",
         month: "numeric",
         day: "2-digit",
         hour: "2-digit",
         minute: "2-digit"
      }).format(date);
   }

   getCurrentDate(initDate) {
      let today = new Date();
      if (initDate) {
         today.setTime(initDate);
      }
      return today.getDate() + '.' + (today.getMonth() + 1) + '.' + today.getFullYear();
   }
}


export default InverterApp