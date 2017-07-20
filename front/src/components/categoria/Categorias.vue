<template>
  <ProtectedRoute>
    <CategoriaForm :categoriaPai="null"></CategoriaForm>
    <CategoriaListt v-for="c in list" :key="c.id" :categoria="c"></CategoriaListt>
  </ProtectedRoute>
</template>

<script>
import ProtectedRoute from '../ProtectedRoute';
import CategoriaForm from './CategoriaForm';
import CategoriaList from './CategoriaList';
import CategoriaListt from './CategoriaListt';
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
    CategoriaList,
    CategoriaListt
  }
}
</script>

<style>
  
</style>

