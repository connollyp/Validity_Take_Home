import React, { Component } from 'react';
import { getNonDuplicates } from "../actions/duplicateActions";

class nonDuplicates extends Component {
  constructor(props) {
    super(props);
    this.state = {
      Entries: 'No entires from server'
    };
  }

  componentDidMount() {
    this._isMounted = true;
    getNonDuplicates().then(Entries => {
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

export default nonDuplicates;