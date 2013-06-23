package spray.examples

trait UserDef extends AddressDef {
  case class User(name: String, age: Int, profession: String, address: Address)
}
