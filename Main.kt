package contacts


import java.io.*
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.system.exitProcess



val contactList = mutableListOf<Contact.Contact>()


fun main(args: Array<String>) {
    contactList.addAll(Contact(args).readFromFile())
    Contact(args).action()
}

class Contact(args: Array<String>) {

    private val filePath = if (args.size == 1) args[0] else null

    fun menu(list: List<Contact>, prompt: String, function: () -> Unit) {
        while (true) {
            print(
                if (prompt != "list") {
                    "[$prompt] Enter action ([number], back, again): "
                } else {
                    "[$prompt] Enter action ([number], back): "
                }
            )
            when (val input = readln().lowercase()) {
                in (1..1000).map { it.toString() } -> {
                    if (input.toInt() - 1 >= list.size) {
                        println("No records!")
                        continue
                    }
                    val contact = list[input.toInt() - 1]
                    showContact(contactList.indexOf(contact))
                    records(contact)

                }

                "back" -> {
                    println()
                    return
                }

                "again" -> {
                    function()
                    return
                }
            }
        }
    }

    private fun records(input: Contact) {
        while (true) {
            print("[record] Enter action (edit, delete, menu): ")
            when (readln()) {
                "edit" -> edit(input)
                "delete" -> remove(input)
                "menu" -> {
                    println()
                    action()
                }

            }
        }
    }

    data class Contact(
        var name: String, var surname: String, var number: String, val type: String,
        var birth: String = "", var gender: String = "", var address: String = "",
        val crate: String, var edit: String
    ) : Serializable


    private val phoneRegex = Regex("""\+?\w*(\d[ -])?(\(\w{2,}\))?(\w{2,}[ -]\(\w{2,} \))?(\b\w{2,})?([ -]\w{2,})*""")

    fun action() {

        while (true) {
            print("[menu] Enter action (add, list, search, count, exit): ")
            when (readln()) {
                "add" -> addNew()
                "count" -> count()
                "list" -> {
                    list()
                    println()
                }

                "search" -> {
                    Share().share()

                }

                "info" -> info()
                "exit" -> exitProcess(0)
            }
        }
    }

    open inner class Share {

        fun share() {
            print("Enter search query: ")
            val query = readln().lowercase()
            val shareList =
                contactList.filter { it.name.lowercase().contains(query) || it.surname.lowercase().contains(query) || it.number.lowercase().contains(query) }
            println("Found ${shareList.size} results:")
            if (shareList.isEmpty()) return
            shareList.mapIndexed { index, contact -> println("${index + 1}. ${contact.name} ${contact.surname}") }
            println()
            menu(shareList, "share", ::share)
        }


    }

    private fun info() {
        if (contactList.size == 0) {
            println("No records to edit!")
            println()
            return
        }
        list()
        println("Enter index to show info:")
        try {
            val index = readln().toInt() - 1
            if (index > contactList.size - 1) return
            if (contactList[index].type == "person") {
                println(
                    "Name: ${contactList[index].name}\n" +
                            "Surname: ${contactList[index].surname}\n" +
                            "Birth date: ${contactList[index].birth}\n" +
                            "Gender: ${contactList[index].gender}\n" +
                            "Number: ${contactList[index].number}\n" +
                            "Time created: ${contactList[index].crate}\n" +
                            "Time last edit: ${contactList[index].edit}"
                )
            } else {
                println(
                    "Organization name: ${contactList[index].name}\n" +
                            "Address: ${contactList[index].address}\n" +
                            "Number: ${contactList[index].number}\n" +
                            "Time created: ${contactList[index].crate}\n" +
                            "Time last edit: ${contactList[index].edit}"
                )
            }
        } catch (e: Exception) {
            println("Wrong index")
            println()
            return
        }
        println()
    }

    private fun showContact(index: Int) {
        if (index > contactList.size - 1) return
        if (contactList[index].type == "person") {
            println(
                "Name: ${contactList[index].name}\n" +
                        "Surname: ${contactList[index].surname}\n" +
                        "Birth date: ${contactList[index].birth}\n" +
                        "Gender: ${contactList[index].gender}\n" +
                        "Number: ${contactList[index].number}\n" +
                        "Time created: ${contactList[index].crate}\n" +
                        "Time last edit: ${contactList[index].edit}"
            )
        } else {
            println(
                "Organization name: ${contactList[index].name}\n" +
                        "Address: ${contactList[index].address}\n" +
                        "Number: ${contactList[index].number}\n" +
                        "Time created: ${contactList[index].crate}\n" +
                        "Time last edit: ${contactList[index].edit}"
            )
        }

        println()
    }


    private fun edit(input: Contact) {
        val record = contactList.indexOf(input)
        try {
            if (record > contactList.size - 1) return
            if (contactList[record].type == "person") {
                println("Select a field (name, surname, birth, gender, number):")
                when (readln().lowercase()) {
                    "name" -> {
                        println("Enter the name of the person:")
                        contactList[record].name = readln()
                    }

                    "surname" -> {
                        println("Enter the surname of the person:")
                        contactList[record].surname = readln()
                    }

                    "number" -> {
                        println("Enter the number:")
                        val number = phoneCheck(readln())
                        contactList[record].number = number
                    }

                    "birth" -> {
                        println("Enter the birth date:")
                        val data = dataCheck(readln().uppercase())
                        contactList[record].birth = data
                    }

                    "gender" -> {
                        println("Enter the gender (M, F):")
                        val gander = ganderCheck(readln().uppercase())
                        contactList[record].gender = gander
                    }

                }
                contactList[record].edit = LocalDateTime.now().toString()
            } else {
                println("Select a field (name, address, number):")
                when (readln()) {
                    "name" -> {
                        println("Enter the name:")
                        contactList[record].name = readln()
                    }

                    "address" -> {
                        println("Enter address:")
                        contactList[record].address = readln()

                    }

                    "number" -> {
                        println("Enter the number:")
                        val number = phoneCheck(readln())
                        contactList[record].number = number
                    }

                }
                contactList[record].edit = LocalDateTime.now().toString()
            }
        } catch (e: Exception) {
            println("Wrong record")
            println()
            return
        }
        saveToFile()
        showContact(contactList.indexOf(input))

    }

    private fun count() {
        println("The Phone Book has ${contactList.size} records.")
        println()
    }

    private fun remove(input: Contact) {
        val record = contactList.indexOf(input)
        try {
            contactList.removeAt(record)
        } catch (e: Exception) {
            println("Wrong record")
            return
        }
        println("The record removed!")
        saveToFile()
        println()

    }


    private fun addNew() {
        println("Enter the type (person, organization):")
        when (readln().lowercase()) {
            "person" -> person()
            "organization" -> organization()
            else -> person()
        }
    }


    private fun person() {
        println("Enter the name of the person:")
        val name = readln()
        println("Enter the surname of the person:")
        val surname = readln()
        println("Enter the birth date:")
        val data = dataCheck(readln().uppercase())
        println("Enter the gender (M, F):")
        val gander = ganderCheck(readln().uppercase())

        println("Enter the number:")
        val number = phoneCheck(readln())
        val time = LocalDateTime.now().toString()
        contactList.add(Contact(name, surname, number, "person", data, gander, crate = time, edit = time))
        saveToFile()
        println()
    }

    private fun organization() {
        println("Enter the organization name:")
        val name = readln()
        println("Enter the address:")
        val address = readln().ifEmpty { "[no data]" }
        println("Enter the number:")
        val number = phoneCheck(readln())
        val time = LocalDateTime.now().toString()
        contactList.add(Contact(name, surname = "", number, "organization", crate = time, edit = time, address = address))
        saveToFile()
        println()
    }


    private fun list() {
        contactList.mapIndexed { index, contact -> println("${index + 1}. ${contact.name} ${contact.surname}, ${contact.number}") }
        println()
        menu(contactList, "list", ::list)

    }

    private fun dataCheck(data: String): String {
        return try {
            LocalDate.parse(data).toString()
        } catch (e: Exception) {
            println("Bad birth date!")
            "[no data]"
        }
    }

    private fun ganderCheck(gander: String): String {
        return when (gander) {
            "M" -> "M"
            "F" -> "F"
            else -> {
                println("Bad gender!")
                "[no data]"
            }
        }

    }

    private fun phoneCheck(number: String): String {
        return if (number.matches(phoneRegex)) number else {
            println("Wrong number format!")
            "[no number]"
        }
    }

    private fun saveToFile() {
        if (filePath == null) {
            println("Saved")
            return
        }

        val file = File(filePath)
        if (file.exists()) file.delete()
        val fileOutputStream = FileOutputStream(filePath)
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(contactList)
        objectOutputStream.close()





        println("Saved")

    }

    fun readFromFile(): List<Contact> {
        if (filePath == null) {
            return emptyList()
        }
        val file = File(filePath)
        return if (file.exists()) {
            val fileInputStream = FileInputStream(file)
            val objectInputStream = ObjectInputStream(fileInputStream)
            val contacts = objectInputStream.readObject()
            objectInputStream.close()

            if (contacts is List<*> && contacts.all { it is Contact }) {
                @Suppress("UNCHECKED_CAST")
                contacts as List<Contact>
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
    }




}