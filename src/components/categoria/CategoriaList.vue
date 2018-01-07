<template>
  <div>
    <div @mouseover="hovered" @mouseleave="unhovered" class="categoria">
      <CategoriaIcone :aberto="aberto" :categoria="categoria" @abreFecha="abre"></CategoriaIcone>
      <span @click="dialogEdit = true">{{ categoria.nome }}</span>
      <transition name="fade">
        <v-btn v-if="showActions" @click.native.stop="dialog = true" color="primary" fab small dark class="small-fab-btn">
          <v-icon>add</v-icon>
        </v-btn>
      </transition>
      <transition name="fade">
        <v-btn v-if="showActions" @click.native.stop="showModalRemove" fab small dark class="small-fab-btn red">
          <v-icon>remove</v-icon>
        </v-btn>
      </transition>
      <v-dialog v-model="dialog">
          <v-card >
            <v-card-title>
              <div class="headline">Nova sub-categoria de {{ categoria.nome }}</div>
            </v-card-title>
            <v-card-text>
              <CategoriaForm ref="filhaForm" :categoria="{ nome: '', pai: categoria.id}" @cadastrado="dialog = false"></CategoriaForm>
            </v-card-text>
          </v-card>
      </v-dialog>
      <v-dialog v-model="dialogEdit">
          <v-card >
            <v-card-title>
              <div class="headline">Editando categoria {{ categoria.nome }}</div>
            </v-card-title>
            <v-card-text>
              <CategoriaForm ref="filhaForm" :categoria="categoria" @cadastrado="dialogEdit = false"></CategoriaForm>
            </v-card-text>
          </v-card>
      </v-dialog>
    </div>
    <div v-if="aberto" style="margin-left: 25px">
      <CategoriaList v-for="f in getFilhas(categoria.id)" :key="f.id" :categoria="f"></CategoriaList>
    </div>
  </div>
</template>

<script>
import CategoriaForm from './CategoriaForm';
import CategoriaIcone from './CategoriaIcone';

export default {
  props: ['categoria'],
  name: 'CategoriaList',
  data() {
    return {
      dialog: false,
      dialogEdit: false,
      aberto: true,
      showActions: false
    }
  },
  watch: {
    dialog(val) {
      if (val) {
        this.$refs.filhaForm.showing();
      } else {
        this.$refs.filhaForm.reset();
      }
    }
  },
  methods: {
    getFilhas(idCategoria) {
      return this.$store.getters.getCategoriasFilhas(idCategoria);
    },
    abre() {
      this.aberto = !this.aberto;
    },
    hovered(e) {
      e.stopPropagation();
      this.showActions = true;
    },
    unhovered() {
      this.showActions = false;
    },
    showModalRemove() {
      alert("não implementado ainda :D")
    }
  },
  components: {
    CategoriaForm,
    CategoriaIcone
  }

}
</script>

<style>
.categoria {
  height: 30px;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity .2s
}
.fade-enter, .fade-leave-to {
  opacity: 0
}
</style>
