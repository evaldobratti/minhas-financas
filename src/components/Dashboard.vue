<template>
  <ProtectedRoute>
      <ContasDashboard></ContasDashboard>
      <entrada-saida></entrada-saida>
  </ProtectedRoute>
</template>

<script>
import ProtectedRoute from './ProtectedRoute';
import ContasDashboard from './conta/ContasDashboard';
import EntradaSaida from './analise/EntradaSaida';

export default {
  components: {
    ProtectedRoute,
    ContasDashboard,
    EntradaSaida
  }
}
</script>

<style>
  
</style>
