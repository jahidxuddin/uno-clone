package view.card

val UNO_DECK = ArrayDeque<String>().apply {
    val allCards = buildList {
        for (color in listOf("blue", "green", "red", "yellow")) {
            add("$color/0_$color.png")
            for (number in 1..9) {
                repeat(2) { add("$color/${number}_$color.png") }
            }
            repeat(2) {
                add("$color/2plus_$color.png")
                add("$color/block_$color.png")
                add("$color/inverse_$color.png")
            }
        }

        repeat(4) {
            add("wild/wild_card.png")
            add("wild/4_plus.png")
        }
    }.shuffled()

    addAll(allCards)
}
