<template>
  <ProtectedRoute>
    <CategoriaForm :categoriaPai="null"></CategoriaForm>
    <v-expansion-panel expand>
      <CategoriaList v-for="categoria in list" :key="categoria.id" :categoria="categoria"></CategoriaList>
    </v-expansion-panel>
  </ProtectedRoute>
</template>

<script>
import ProtectedRoute from '../ProtectedRoute';
import CategoriaForm from './CategoriaForm';
import CategoriaList from './CategoriaList';
import { m, d } from '../../store/categorias';
import { mapState } from 'vuex';

export default {
  created() {
    this.$store.dispatch(d.LOAD_CATEGORIAS);
  },
  computed: mapState({
    list: state => state.categorias.list
  }),
  components: {
    ProtectedRoute,
    CategoriaForm,
    CategoriaList
  }
}
</script>

<style>
  
</style>
