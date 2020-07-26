import React, { Component } from 'react';
import { Container } from 'reactstrap';
import Duplicates from '../components/Duplicates';
import NonDuplicates from '../components/nonDuplicates'

class DuplicatesPage extends Component {
  render() {
    return (
      <div className='app'>
        <div className='app-body'>
          <Container fluid className='text-center'>
              <Container>
                <b> Duplicate Entries</b>
                <Duplicates />

                <br/>

                <b>Non Duplicate Entries</b>
                <NonDuplicates />
              </Container>
          </Container>
        </div>
      </div>
    );
  }
}

export default DuplicatesPage;