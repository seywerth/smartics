import React, { Component } from 'react';
import { scaleLinear } from 'd3-scale';
import { select } from 'd3-selection';
import { max } from 'd3-array';

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
      const maxY = max(this.props.data, d => d[2]);
	  const maxX = max(this.props.data, d => d[0]); //12 * 24;
      const yScale = scaleLinear()
         .domain([0, maxY])
         .range([0, this.props.size[1]]);
	  const xScale = scaleLinear()
		 .domain([0, maxX])
		 .range([0, this.props.size[0]]);

   	  select(node)
      	.selectAll('rect')
      	.data(this.props.data)
      	.enter()
      	.append('rect');

   	  select(node)
      	.selectAll('rect')
      	.data(this.props.data)
      	.exit()
      	.remove();

	  select(node)
      	.selectAll('rect')
      	.data(this.props.data)
      	.style('fill', '#fe9922')
      	.attr('x', d => this.props.size[0] - xScale(d[0]))
      	.attr('y', d => this.props.size[1] - yScale(d[2]))
      	.attr('height', d => yScale(d[2]))
      	.attr('width', 2);

    }

	render() {
      	return (
			<svg ref={node => this.node = node} width={this.props.size[0]} height={this.props.size[1]}>
      		</svg>
		)
    }
}

export default EnergyChart