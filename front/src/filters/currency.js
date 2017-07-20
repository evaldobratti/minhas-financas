export default (value) => {
  if (!value || !Number(value))
    return '';
  
  return 'R$ ' + value.toFixed(2);
}