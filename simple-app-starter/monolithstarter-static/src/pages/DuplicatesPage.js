import React, { Component } from 'react';
import { Container } from 'reactstrap';
import Duplicates from '../components/Duplicates';
import nonDuplicates from '../components/nonDuplicates'

class DuplicatePage extends Component {
  render() {
    return (
      <div className='app'>
        <div className='app-body'>
          <Container fluid className='text-center'>
              <Container className='duplicate-container'>
                <Duplicates />
               </Container>
               <Container className='nonduplicate-container'>
                <nonDuplicates />
               </Container>
          </Container>
        </div>
      </div>
    );
  }
}

export default DuplicatePage;