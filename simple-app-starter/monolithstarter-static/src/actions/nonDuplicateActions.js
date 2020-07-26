import axios from 'axios';

export async function getNonDuplicates() {
  return (await axios.get('/api/nonDuplicate')).data;
}
