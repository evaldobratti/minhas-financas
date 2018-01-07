export default (value) => {
  if (!value)
    return '';
  
  return value.format('DD/MM/YYYY');
}