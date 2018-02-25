<template>
  <ProtectedRoute>
      <ContasDashboard></ContasDashboard>
      <entrada-saida></entrada-saida>
      <gastos></gastos>
  </ProtectedRoute>
</template>

<script>
import ProtectedRoute from './ProtectedRoute';
import ContasDashboard from './conta/ContasDashboard';
import EntradaSaida from './analise/EntradaSaida';
import Gastos from './analise/Gastos';

export default {
  components: {
    ProtectedRoute,
    ContasDashboard,
    EntradaSaida,
    Gastos
  }
}
</script>

<style>
  
</style>
