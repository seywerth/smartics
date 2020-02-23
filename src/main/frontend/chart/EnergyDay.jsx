import React, { Component } from 'react';
import { scaleLinear, scaleBand } from 'd3-scale';
import { select } from 'd3-selection';
import { axisBottom, axisLeft } from 'd3-axis';
import { line } from 'd3-shape';

class EnergyDay extends Component {

   constructor(props) {
      super(props);
      this.createBarChart = this.createBarChart.bind(this);
   }

   componentDidMount() {
      this.createBarChart();
   }

   componentDidUpdate() {
      this.createBarChart();
   }

   createBarChart() {
      const node = this.node;
      // max Wh produced or used (5500Wh vs 500Wh in 5min = 6kWh)
      const maxY = this.props.maxwh;
      // max entries for one day
      const maxX = 12 * 24;
      let margin = { top: 10, right: 10, bottom: 30, left: 30 },
         width = this.props.size[0] - margin.left - margin.right,
         height = this.props.size[1] - margin.top - margin.bottom;
      const xScale = scaleLinear()
         .domain([0, maxX])
         .range([margin.left, margin.left + width]);
      const yScale = scaleLinear()
         .domain([0, maxY])
         .range([height, 0]);

      // cleanup elements inside svg
      select(node)
         .selectAll('g')
         .remove();
      select(node)
         .selectAll('rect')
         .remove();
      select(node)
         .selectAll('circle')
         .remove();
      select(node)
         .selectAll('path')
         .remove();

      const consumedData = this.getArrayForConsumedData(this.props.data);
      const producedData = this.getArrayForProducedData(this.props.data);

      // show production
      const rectWidth = (this.props.size[0] > 700) ? 2 : 1;
      select(node)
         .selectAll('svg')
         .data(producedData)
         .enter()
         .append('rect')
         .style('fill', '#fe9922')
         .attr('x', d => xScale(d.time))
         .attr('y', d => yScale(d.produced) + margin.top)
         .attr('height', d => height - yScale(d.produced))
         .attr('width', rectWidth);

      // show consumption
      select(node)
         .selectAll('svg')
         .data(consumedData)
         .enter()
         .append('circle')
         .style('fill', '#bb4444')
         .attr('cx', d => xScale(d.time))
         .attr('cy', d => yScale(d.consumed) + margin.top)
         .attr('r', 2)
         .style("opacity", .7);

      // mark extreme values, consumption too high for graph
      select(node)
         .selectAll('svg')
         .data(consumedData.filter(ar => ar.consumed >= this.props.maxwh))
         .enter()
         .append('circle')
         .style('stroke', '#994444')
         .style('fill', 'none')
         .attr('cx', d => xScale(d.time))
         .attr('cy', d => yScale(d.consumed) + margin.top)
         .attr('r', 4)
         .style("opacity", .7);

      // connect lines of consumption
      const lineFunction = line()
         .x(d => xScale(d.time))
         .y(d => yScale(d.consumed) + margin.top);

      select(node)
         .selectAll('path')
         .data([consumedData])
         .enter()
         .append('path')
         .attr('fill', 'none')
         .attr('stroke', '#bb4444')
         .attr('stroke-width', 1.5)
         .attr('d', lineFunction(consumedData))
         .style('opacity', .5);

      // mark status of values (OK/OK_PARTIAL/NOT_ENOUGH_DATA)
      const status = this.getArrayForStatus(this.props.data);
      select(node)
         .selectAll('svg')
         .data(status.filter(ar => ar.status === 'OK' || ar.status === 'OK_PARTIAL'))
         .enter()
         .append('circle')
         .style('fill', '#00ff00')
         .attr('cx', d => xScale(d.time))
         .attr('cy', height + margin.top + 2)
         .attr('r', 2)
         .style("opacity", .7);
      select(node)
         .selectAll('svg')
         .data(status.filter(ar => ar.status !== 'OK' && ar.status !== 'OK_PARTIAL' && ar.status !== 'NOT_ENOUGH_DATA'))
         .enter()
         .append('circle')
         .style('stroke', '#666666')
         .style('stroke-width', '2')
         .style('fill', 'none')
         .attr('cx', d => xScale(d.time))
         .attr('cy', height + margin.top + 2)
         .attr('r', 3)
         .style("opacity", .7);
      select(node)
         .selectAll('svg')
         .data(status.filter(ar => ar.status === 'NOT_ENOUGH_DATA'))
         .enter()
         .append('circle')
         .style('stroke', '#bb00ff')
         .style('stroke-width', '2')
         .style('fill', 'none')
         .attr('cx', d => xScale(d.time))
         .attr('cy', height + margin.top + 2)
         .attr('r', 3)
         .style("opacity", .7);

      // prepare axis
      let yAxis = axisLeft()
         .scale(yScale)
         .ticks(12, "s")
         .tickSize(-width, 0, 0);

      const scaleTitle = scaleBand()
         .domain(this.get24hrsArray())
         .range([margin.left, margin.left + width])
         .paddingInner(10);
      let xAxis = axisBottom(scaleTitle)
         .tickSizeOuter(0);

      // show axis
      select(node)
         .append('g')
         .attr('transform', `translate(0,${margin.top + height})`)
         .call(xAxis)
         .selectAll("text")
         .attr("transform", "translate(-5,5)rotate(-45)")
         .style("text-anchor", "end");

      select(node)
         .append('g')
         .attr('transform', `translate(${margin.left},${margin.top})`)
         .call(yAxis)
         .attr("class", "grid");

      // show current time pointer
      if (this.props.showline) {
         select(node)
            .select('rect')
            .style('fill', '#4444aa')
            .attr('x', margin.left + this.getCurrentPos(width))
            .attr('y', margin.top - 5)
            .attr('height', height + 15)
            .attr('width', 1.5);
      }
   }

   render() {
      return (
         <svg ref={node => this.node = node} width={this.props.size[0]} height={this.props.size[1]}>
         </svg>
      )
   }

   getArrayForConsumedData(data) {
      let consumedArray = data.map(el => {
         let consumed = el[1];
         if (el[1] > this.props.maxwh) {
            consumed = this.props.maxwh;
         }
         return { time: el[0], consumed: consumed };
      });
      return consumedArray;
   }

   getArrayForProducedData(data) {
      let producedArray = data.map(el => { return { time: el[0], produced: el[2] }; });
      let resultArray = producedArray.filter(ar => ar.produced > 0);
      return resultArray;
   }

   getArrayForStatus(data) {
      let statusArray = data.map(el => { return { time: el[0], status: el[3] }; });
      return statusArray;
   }

   getCurrentPos(width) {
      if (width == undefined) {
         return 0;
      }
      const oneMin = width / (60 * 24);
      const date = new Date();
      //console.log("currentpos: " + (date.getHours() * 60 + date.getMinutes()) * oneMin);
      return (date.getHours() * 60 + date.getMinutes()) * oneMin;
   }

   get24hrsArray() {
      let times = [];
      if (this.props.size[0] > 550) {
         for (let i = 0; i <= 24; i++) {
            times[i] = i + "h";
         }
      } else {
         times = ['0h', '3h', '6h', '9h', '12h', '15h', '18h', '21h', '24h'];
      }
      return times;
   }
}

export default EnergyDay;