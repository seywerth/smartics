import React, { Component } from 'react';
import { scaleLinear, scaleBand } from 'd3-scale';
import { select } from 'd3-selection';
import { axisBottom, axisLeft } from 'd3-axis';
import { line } from 'd3-shape';

class EnergyChart extends Component {

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
      const maxY = 5500;
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

      select(node)
         .selectAll('rect')
         .data(this.props.data)
         .enter()
         .append('rect');

      select(node)
         .selectAll('circle')
         .data(this.props.data)
         .enter()
         .append('circle');

      select(node)
         .selectAll('path')
         .data(this.props.data)
         .enter()
         .append('path');

      // show production
      select(node)
         .selectAll('rect')
         .data(this.props.data)
         .style('fill', '#fe9922')
         .attr('x', d => xScale(d[0]))
         .attr('y', d => yScale(d[2]) + margin.top)
         .attr('height', d => height - yScale(d[2]))
         .attr('width', 2);

      // show consumption
      select(node)
         .selectAll('circle')
         .data(this.props.data)
         .style('fill', '#bb4444')
         .attr('cx', d => xScale(d[0]))
         .attr('cy', d => yScale(d[1]) + margin.top)
         .attr('r', 2)
         .style("opacity", .7);

      const lineFunction = line()
         .x(d => xScale(d.time))
         .y(d => yScale(d.consumed) + margin.top);

      select(node)
         .select('path')
         .attr('fill', "none")
         .attr('stroke', '#bb4444')
         .attr('stroke-width', 1.5)
         .attr('d', lineFunction(this.getArrayObjectForEnergy(this.props.data)))
         .style('opacity', .5);

      // prepare axis
      let yAxis = axisLeft()
         .scale(yScale)
         .ticks(10, "s")
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

   getArrayObjectForEnergy(data) {
      let ret = data.map(el => { return { time: el[0], consumed: el[1] }; });
      //console.log(ret);
      return ret;
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
      for (let i = 0; i <= 24; i++) {
         times[i] = i + "h";
      }
      return times;
   }
}

export default EnergyChart