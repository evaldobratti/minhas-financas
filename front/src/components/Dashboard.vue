<template>
  <ProtectedRoute>
      <ContasDashboard></ContasDashboard>
  </ProtectedRoute>
</template>

<script>
import ProtectedRoute from './ProtectedRoute';
import ContasDashboard from './conta/ContasDashboard';

export default {
  components: {
    ProtectedRoute,
    ContasDashboard
  }
}
</script>

<style>
  
</style>
