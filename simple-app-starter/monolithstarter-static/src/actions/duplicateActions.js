import axios from 'axios';

export async function getDuplicates() {
  return (await axios.get('/api2/duplicate').data;
}

export async function getNonDuplicates() {
  return (await axios.get('/api2/nonDuplicate').data;
}