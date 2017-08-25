<template>
  <div>
    <div @mouseover="hovered" @mouseleave="unhovered" class="categoria">
      <CategoriaIcone :aberto="aberto" :categoria="categoria" @abreFecha="abre"></CategoriaIcone>
      <span @click="abre">{{ categoria.nome }}</span>
      <transition name="fade">
        <v-btn v-if="showActions" @click.native.stop="showModalAdd" primary fab small dark class="small-fab-btn">
          <v-icon>add</v-icon>
        </v-btn>
      </transition>
      <transition name="fade">
        <v-btn v-if="showActions" @click.native.stop="showModalRemove" primary fab small dark class="small-fab-btn red">
          <v-icon>remove</v-icon>
        </v-btn>
      </transition>
      <v-dialog v-model="dialog">
          <v-card >
            <v-card-title>
              <div class="headline">Nova sub-categoria de {{ categoria.nome }}</div>
            </v-card-title>
            <v-card-text>
              <CategoriaForm :categoriaPai="categoria" @cadastrado="dialog = false"></CategoriaForm>
            </v-card-text>
          </v-card>
      </v-dialog>
    </div>
    <div v-if="aberto" style="margin-left: 25px">
      <CategoriaList v-for="f in categoria.filhas" :key="f.id" :categoria="f"></CategoriaList>
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
      aberto: true,
      showActions: false
    }
  },
  methods: {
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
    showModalAdd() {
      this.dialog = true;
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
