package fledware.definitions.loadlist

interface LoadListProcessor {
  fun init(manager: LoadListManager)
  fun process(context: LoadListContext)
}