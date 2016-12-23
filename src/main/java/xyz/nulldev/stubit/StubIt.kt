package xyz.nulldev.stubit

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ThrowStmt
import com.github.javaparser.ast.type.PrimitiveType
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import java.io.File
import java.util.*

class StubIt {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            StubIt().run(args)
        }
    }

    fun run(args: Array<String>) {
        var print = false
        var extension = "stub.java"
        var stdin = false

        val files = mutableListOf<File>()

        var expectExtension = false
        for(arg in args) {
            if(expectExtension) {
                extension = arg
                expectExtension = false
            } else if(arg == "--stdin") {
                stdin = true
            } else if(arg == "--print") {
                print = true
            } else if(arg == "--extension") {
                expectExtension = true
            } else {
                files += File(arg)
            }
        }
        if(expectExtension) {
            println("Error: Expecting file extension!")
            return
        }

        val visitor = StubVisitor()
        fun processCu(cu: CompilationUnit, output: File? = null) {
            visitor.visit(cu, null)

            if(output != null) {
                output.writeText(cu.toString())
            } else {
                System.out.println(cu)
            }
        }

        if(stdin) {
            val list = mutableListOf<String>()
            val s = Scanner(System.`in`)
            while(s.hasNextLine()) {
                list += s.nextLine()
            }
            val string = list.joinToString("\n")
            val cu = JavaParser.parse(string)
            processCu(cu)
        } else {
            for(file in files) {
                val cu = JavaParser.parse(file)

                processCu(cu, if(!print)
                    File(file.parentFile, "${file.nameWithoutExtension}.$extension")
                else
                    null)
            }
        }
    }

    class StubVisitor : VoidVisitorAdapter<Void>() {

        val RUNTIME_EXCEPTION_STMT = ThrowStmt(ObjectCreationExpr()
                .setType(RuntimeException::class.java)
                .addArgument(StringLiteralExpr("Stub!")))

        val RUNTIME_EXCEPTION_BLK = BlockStmt().addStatement(RUNTIME_EXCEPTION_STMT)!!

        override fun visit(n: MethodDeclaration, arg: Void?) {
            if(n.body.isPresent) n.setBody(RUNTIME_EXCEPTION_BLK)
        }

        override fun visit(n: FieldDeclaration, arg: Void?) {
            if(n.variables.isEmpty()) return

            for(variable in n.variables) {
                if(!variable.initializer.isPresent) {
                    continue
                }
                val init = variable.initializer.get()

                //Null out any dynamic initializer
                if(isDynamic(init)) {
                    val expr = if(n.commonType is PrimitiveType) {
                        when(n.commonType) {
                            PrimitiveType.BOOLEAN_TYPE -> BooleanLiteralExpr()
                            PrimitiveType.CHAR_TYPE -> CharLiteralExpr()
                            else -> IntegerLiteralExpr()
                        }
                    } else {
                        NullLiteralExpr()
                    }
                    variable.setInitializer(expr)
                }
            }
        }

        override fun visit(n: ConstructorDeclaration, arg: Void?) {
            n.body = RUNTIME_EXCEPTION_BLK
        }

        override fun visit(n: BlockStmt, arg: Void?) {
            n.statements.clear()
        }

        fun isDynamic(n: Expression?): Boolean {
            if(n == null) {
                return false
            } else if(n is BinaryExpr) {
                return isDynamic(n.left) || isDynamic(n.right)
            } else if(n is CastExpr) {
                return isDynamic(n.expression)
            } else if(n is ArrayCreationExpr) {
                if(n.initializer.isPresent)
                    return isDynamic(n.initializer.get())
            } else if(n is ArrayInitializerExpr) {
                n.values
                        .filter { isDynamic(it) }
                        .forEach { return true }
            } else if(n is ClassExpr) {
                return false
            } else if(n is ConditionalExpr) {
                return isDynamic(n.condition) || isDynamic(n.elseExpr) || isDynamic(n.thenExpr)
            } else if(n is EnclosedExpr) {
                if(n.inner.isPresent) return isDynamic(n.inner.get())
            } else if(n is ArrayAccessExpr) {
                return isDynamic(n.name) || isDynamic(n.index)
            } else if(n is UnaryExpr) {
                when(n.operator) {
                    UnaryExpr.Operator.PREFIX_DECREMENT,
                    UnaryExpr.Operator.POSTFIX_DECREMENT,
                    UnaryExpr.Operator.PREFIX_INCREMENT,
                    UnaryExpr.Operator.POSTFIX_INCREMENT -> return false
                    else -> return isDynamic(n.expression)
                }
            } else return n !is LiteralExpr
            return false
        }
    }
}
