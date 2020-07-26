import React, { Component } from 'react';
import { getDuplicates } from "../actions/duplicateActions";

class Duplicates extends Component {
  constructor(props) {
    super(props);
    this.state = {
      Entries: 'No entires from server'
    };
  }

  componentDidMount() {
    this._isMounted = true;
    getDuplicates().then(Entries => {
      if (this._isMounted)
        this.setState({Entries});
    }).catch(() => {
      if (this._isMounted)
        this.setState({Entries: 'The server did not respond so...hello from the client!'});
    });
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  render() {
    return (
      <div>{this.state.Entries}</div>
    );
  }
}

export default Duplicates;