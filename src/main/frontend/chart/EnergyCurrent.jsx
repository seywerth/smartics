import React, { Component } from 'react';
import { scaleLinear, scaleBand } from 'd3-scale';
import { select } from 'd3-selection';
import { axisBottom, axisLeft } from 'd3-axis';

class EnergyCurrent extends Component {

   constructor(props) {
      super(props);
      this.createBarChart = this.createBarChart.bind(this);
   }

   componentDidMount() {
      //console.log("componentDidMount");
   }

   componentDidUpdate() {
      this.createBarChart();
   }

   createBarChart() {
      const node = this.node;
      // max Wh produced or used (5500Wh vs 500Wh in 5min = 6kWh)
      // use max of prod/consumption
      //const maxY = this.props.data.produced > this.props.data.consumed ? this.props.data.produced : this.props.data.consumed; //this.props.maxwh;
      const maxY = this.props.maxwh;
      // entries: consumption, production, feedback
      const maxX = 3;
      let margin = { top: 5, right: 5, bottom: 30, left: 30 },
         width = this.props.size[0] - margin.left - margin.right,
         height = this.props.size[1] - margin.top - margin.bottom;
      const xScale = scaleLinear()
         .domain([0, maxX])
         .range([0, width]);
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

      // show consumption
      select(node)
         .selectAll('svg')
         .data([this.props.data.consumed])
         .enter()
         .append('rect')
         .style('fill', '#bb4444')
         .attr('x', xScale(0.1) + margin.left)
         .attr('y', d => yScale(d) + margin.top)
         .attr('height', d => height - yScale(d))
         .attr('width', xScale(0.8));

      // show production
      select(node)
         .selectAll('svg')
         .data([this.props.data.produced])
         .enter()
         .append('rect')
         .style('fill', '#fe9922')
         .attr('x', xScale(1.1) + margin.left)
         .attr('y', d => yScale(d) + margin.top)
         .attr('height', d => height - yScale(d))
         .attr('width', xScale(0.8));

      // show feedback
      select(node)
         .selectAll('svg')
         .data([this.props.data.feedback])
         .enter()
         .append('rect')
         .style('fill', '#bbddbb')
         .attr('x', xScale(2.1) + margin.left)
         .attr('y', d => yScale(d) + margin.top)
         .attr('height', d => height - yScale(d))
         .attr('width', xScale(0.8));

      // prepare axis
      let yAxis = axisLeft()
         .scale(yScale)
         .ticks(6, "s");

      const scaleTitle = scaleBand()
         .domain(this.getTitleXAxis())
         .range([margin.left, margin.left + width])
         .paddingInner(0);
      let xAxis = axisBottom(scaleTitle)
         .tickSizeOuter(0);

      // show axis
      select(node)
         .append('g')
         .attr('transform', `translate(0,${margin.top + height})`)
         .call(xAxis)
         .selectAll("text")
         .attr("transform", "translate(-5,5)rotate(-35)");

      select(node)
         .append('g')
         .attr('transform', `translate(${margin.left},${margin.top})`)
         .call(yAxis)
         .attr("class", "grid");
   }

   render() {
      return (
         <svg ref={node => this.node = node} width={this.props.size[0]} height={this.props.size[1]}>
         </svg>
      )
   }

   getTitleXAxis() {
      return ['-' + this.props.data.consumed, '+' + this.props.data.produced, '>' + this.props.data.feedback];
   }

}

export default EnergyCurrent;