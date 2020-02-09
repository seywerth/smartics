import React, { Component } from 'react';
import { scaleLinear } from 'd3-scale';
import { select } from 'd3-selection';
import { axisBottom } from 'd3-axis';

class PowerChart extends Component {

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
      // fixed height
      const maxY = this.props.size[1];
      // total Wh: fromgrid + fromprod + feedback
      const maxX = this.props.data.fromgrid + this.props.data.fromprod + this.props.data.feedback;
      let margin = { top: 10, right: 10, bottom: 30, left: 10 },
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

      // show consumption
      select(node)
         .selectAll('svg')
         .data([this.props.data.consumed])
         .enter()
         .append('rect')
         .style('stroke', '#bb4444')
         .style('stroke-width', '2')
         .style('fill', 'none')
         .attr('x', margin.left + 1)
         .attr('y', margin.top)
         .attr('height', yScale(20))
         .attr('width', xScale(this.props.data.consumed) - margin.left);

      // show production
      const prodWidth = xScale(this.props.data.produced) - margin.left;
      if (prodWidth > 0) {
         select(node)
            .selectAll('svg')
            .data([this.props.data.produced])
            .enter()
            .append('rect')
            .style('stroke', '#fe9922')
            .style('stroke-width', '2')
            .style('fill', 'none')
            .attr('x', xScale(this.props.data.fromgrid))
            .attr('y', yScale(70) + margin.top)
            .attr('height', yScale(20))
            .attr('width', prodWidth);
      }

      let xAxis = axisBottom(xScale)
         .ticks(12, "s")
         .tickSizeOuter(0);

      // show axis
      select(node)
         .append('g')
         .attr('transform', `translate(0,${margin.top + height})`)
         .call(xAxis)
         .selectAll("text")
         .attr("transform", "translate(-5,5)rotate(-45)")
         .style("text-anchor", "end");

   }

   render() {
      return (
         <svg ref={node => this.node = node} width={this.props.size[0]} height={this.props.size[1]}>
         </svg>
      )
   }

}

export default PowerChart;