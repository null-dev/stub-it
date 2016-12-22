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
                val output = if(!print)
                    "${file.nameWithoutExtension}.$extension"
                else
                    null

                processCu(cu, File(file.parentFile, output))
            }
        }
    }

    class StubVisitor : VoidVisitorAdapter<Void>() {

        val RUNTIME_EXCEPTION_STMT = ThrowStmt(ObjectCreationExpr()
                .setType(RuntimeException::class.java)
                .addArgument(StringLiteralExpr("Stub!")))

        val RUNTIME_EXCEPTION_BLK = BlockStmt().addStatement(RUNTIME_EXCEPTION_STMT)!!

        override fun visit(n: MethodDeclaration, arg: Void?) {
            n.setBody(RUNTIME_EXCEPTION_BLK)
        }

        override fun visit(n: FieldDeclaration, arg: Void?) {
            if(n.variables.isEmpty()) return

            for(variable in n.variables) {
                if(!variable.initializer.isPresent) {
                    continue
                }
                val init = variable.initializer.get()

                //Null out any dynamic initializer
                if(init !is LiteralExpr) {
                    val expr = if(n.commonType is PrimitiveType)
                        null
                    else
                        NullLiteralExpr()
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
    }
}
