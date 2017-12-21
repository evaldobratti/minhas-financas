<template>
  <ProtectedRoute>
      <v-dialog v-model="dialogRaiz">
        <v-card >
          <v-card-title>
            <div class="headline">Nova categoria raíz</div>
          </v-card-title>
          <v-card-text>
            <CategoriaForm ref="formRaiz" @cadastrado="dialogRaiz = false"></CategoriaForm>
          </v-card-text>
        </v-card>
      </v-dialog>
      <v-btn @click.native.stop="dialogRaiz = true" color="primary" small dark>
        NOVA RAIZ
      </v-btn>

    <CategoriaList v-for="c in list" :key="c.id" :categoria="c"></CategoriaList>
  </ProtectedRoute>
</template>

<script>
import ProtectedRoute from '../ProtectedRoute';
import CategoriaForm from './CategoriaForm';
import CategoriaList from './CategoriaList';
import { m, d } from '../../store/categorias';
import { mapState } from 'vuex';
import DatePicker from '../DatePicker';

export default {
  created() {
    this.$store.dispatch(d.LOAD_CATEGORIAS).catch(()=> {});
  },
  data() {
    return {
      dialogRaiz: false
    }
  },
  watch: {
    dialogRaiz(val) {
      if (val) {
        this.$refs.formRaiz.showing();
      }
    }
  },
  computed: {
    list() {
      return this.$store.state.categorias.list;
    }
  },
  components: {
    ProtectedRoute,
    CategoriaForm,
    CategoriaList,
    DatePicker,
  }
}
</script>

<style>

</style>

