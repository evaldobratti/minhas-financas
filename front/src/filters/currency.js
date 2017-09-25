export default (value) => {
  if (value == null || value == undefined || Number(value) === NaN)
    return '';
  
  return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }).replace('R$', 'R$ ');
}