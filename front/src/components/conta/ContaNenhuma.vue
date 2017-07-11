<template>
  <v-card>
    <v-card-text>
      Nenhuma conta encontrada.
    </v-card-text>
    <v-dialog v-model="novaConta" lazy absolute>
      <v-btn primary dark slot="activator">nova conta</v-btn>
      <ContaForm></ContaForm>
    </v-dialog>
  </v-card>
</template>

<script>
import ContaForm from './ContaForm';

export default {
  data() {
    return {
      novaConta: false
    }
  },
  components: {
    ContaForm
  }
}
</script>

<style>
  
</style>
