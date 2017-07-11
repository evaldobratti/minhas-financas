export default (value) => {
  if (Number(value) === NaN)
    return '';
  
  return 'R$ ' + value.toFixed(2);
}