export default (value) => {
  if (value == null || value == undefined || Number(value) === NaN)
    return '';
  
  return 'R$ ' + value.toFixed(2);
}