# IML to Lustre Generator: Output
The Lustre generator of HERMES emits Lustre encoding of the input model in IML. The Lustre language is a declarative synchronous data flow language designed by the Synchrone Team at Verimag to program reactive systems.  

A lustre program can be used to describe system properties in terms of a set of equations of the program variables, which always hold valid during the course of execution of the system. 
In the following, we briefly recap its syntax in V4 and V6. Lustre supports comments in the following three styles: 

    -- single line comment
    
    /* multi-line 
        comments */
    
    (* Pascal-style 
        comments *)

A Lustre porgram consists of declarations of types and nodes. 
The following code illustrates the structure of a Lustre program.  

    01  type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    02      direction : iml_dot_systems_dot_Direction;
    03      data : int
    04  };

    05  type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    06      direction : iml_dot_systems_dot_Direction;
    07      data : int
    08  };

    09  type iml_dot_systems_dot_Direction = enum {
    10      iml_dot_systems_dot_Direction_dot_IN,
    11      iml_dot_systems_dot_Direction_dot_OUT,
    12      iml_dot_systems_dot_Direction_dot_INOUT
    13  };

    14  node imported agree_dot_example_dot_B (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
    15  returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
    16  (*@contract
    17      guarantee "REQ2" Output.data < Input.data + 15;
    18      assume "REQ1" Input.data  < 20;
    19  *) 

    20  node imported agree_dot_example_dot_C (Input1 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; 
    21                                         Input2 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
    22  returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
    23  (*@contract
    24      guarantee "REQ1" Output.data = Input1.data + Input2.data;
    25  *) 

    26  node imported agree_dot_example_dot_A (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
    27  returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
    28  (*@contract
    29      guarantee "REQ2" Output.data < 2 * Input.data;
    30      assume "REQ1" Input.data < 20;
    31  *) 

    32  node agree_dot_example_dot_top_level_dot_Impl (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
    33  returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
    34  (*@contract
    35      guarantee "REQ2" Output.data < 50;
    36      assume "REQ1" Input.data < 10;
    37  *) 
    38  var A_sub_Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
    39  var B_sub_Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
    40  let
    41      (A_sub_Output) = agree_dot_example_dot_A ( Input);
    42      (Output) = agree_dot_example_dot_C ( A_sub_Output, B_sub_Output);
    43      (B_sub_Output) = agree_dot_example_dot_B ( A_sub_Output);
    44      --%MAIN; 
    45  tel

At the beginning of this example, two record types (`iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__` and `iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__`) and one enumerated type (`iml_dot_systems_dot_Direction`) are introduced. 

Then four node declarations, namely, `agree_dot_example_dot_B`, `agree_dot_example_dot_C`, `agree_dot_example_dot_A` and `agree_dot_example_dot_top_level_dot_Impl` are given. 
A node declaration starts with the keyword `node`, and then is followed by its signature: node name, input arguments, the keyword `returns`, and output arguments. 

A node contract is specified right after its signature. 
Sets of assumptions, guarantees, and modes together define a contract for a node. 
Contracts can be specified locally in an inline format as shown in the above example or externally in a node, which makes it a contract node. 
A local contract of a node is specified as a comment between after its signature and before its `let` body. 
The contact in the comment is annotated with `@contract`. 
In contrast to a local contract, an external contract is specified as a contract node, which starts with the keyword `contract`. 
The body of a contract node can only consist of contract items, including ghost variables, ghost constants, assumptions, guarantees, and modes. 
A contract node is used via importing in an inline contract. 
Assumptions are annotated with the keyword `assume`. 
An assumption of a node is a Boolean expression in terms of the inputs of the node and possibly the previous state values of its outputs via the operator `pre` of Lustre. 
Similarly, guarantees are annotated with the keyword `guarantee`. 
A guarantee of a node is a Boolean expression in terms of its inputs and outputs. 
Modes can be considered as a combination of assumptions and guarantees. 
The semantics of assume-guarantee contracts is that the assumptions of a node specify how the node should be used while its guarantees specify the behavior of the node. 
The contract of a node is respected when all of its executions satisfy the following LTL formula: `G A imply G B`. In other words, the guarantees hold if the assumptions always hold. 

A contract can be imported by another contract. 
The resulted contract is defined by the union of assumption, guarantee and mode of the respective contracts. 

Following the contract section are local declarations and the body of the node. 
Local declarations may introduce constants and variables, both of which are optional. 
Notice that for the node `agree_dot_example_dot_top_level_dot_Impl`, only two local variables are declared. 
The node body is enclosed by keywords `let`  and `tel`. 
Node equations are introduced in the node body. 
Assignment can be made to a list expressed in parentheses. 
Note that node equations are optional as well. 
A node can end with `tel;`, `tel.` or `tel`. 

The main node annotation, `--%MAIN ;`, is used to specify the top-level node of a Lustre program. 
It can be placed anywhere in the node body. 
If the annotation `--%MAIN ;` was not specified, the default top-level node is the last node in the program. 
In the above example, the main node annotation is explicitly used to specify node `agree_dot_example_dot_top_level_dot_Impl` to be the top-level node. 
Even without this annotation, by default, node `agree_dot_example_dot_top_level_dot_Impl` still serves as the main node since it is the last node in the program. 
Notice that the main node annotation is terminated by `;`.  

Besides nodes, functions can also be declared. 
But functions are stateless and therefore temporal operators such as the followed-by operator (`->`), the delay operator (`pre`), and the sampling operator (`when`), etc. cannot be used in functions. 

A node or function can be declared using the keyword `imported`. 
An imported node or function does not have a let body. 
An imported node is abstracted by its contract. 
When no contract is specified, the default one is the weakest contract, i.e., `assume true` and `guarantee true`. 
In the above example, the nodes ` agree_dot_example_dot_B `, ` agree_dot_example_dot_C ` and ` agree_dot_example_dot_A ` are declared `imported`. 
Notice that they only have a contract section. 

You may wonder why the names of some of the identifiers in the above example are so long. 
The above Lustre program actually is the Lustre-encoding that our Lustre generator produced for the example shown in Section [IML to Lustre Generator: Input](./.gen_lustre_input.md.html). 
Fully-qualified names (formed by concatenating names of scope hierarchies) are used to guarantee unique identifiers in the encoding. 

Comparing the above generated Lustre encoding to the input description in IML as shown in Section [IML to Lustre Generator: Input](./.gen_lustre_input.md.html), it is not difficult to conclude that the generated Lustre encoding is structually similar to the original IML description. 
Record type `iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__` (Lines 01 - 04 in the above encoding) is generated for the IML constrained type `InDataPort<Int>` that is used in Lines 08, 17, 27, 34, 47, 49, 51 and 53 of the IML description. 
Similarly, record type `iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__` (Lines 05 - 08 in the above encoding) is generated for the IML constrained type `InDataPort<Int>` that is used in Lines 07, 16, 25, 26, 33, 45, 47, 49 and 51 of the IML description. 
Enumerated type `iml_dot_systems_dot_Direction` (Lines 09 - 13 in the above encoding) is generated for the enumeration type `Direction` defined in package `iml.systems` that is used in the above two record types. 
Node `agree_dot_example_dot_B` (Lines 14 - 19 in the above encoding) is generated for the constrained type `B` used in Line 43 and defined in Line 23 of the IML description. 
Notice that the input and output variables in trait `BTrait` that `B` exhibits are rewritten as input and output arguments of node `imported agree_dot_example_dot_B` to preserve the component interface.
Moreover, the assume and guarantee statements from Line 09 to Line 12 of the IML description are captured by the inlined node contract from Line 16 to Line 19 of the above encoding.
Constrained type `C` and `A` are similarly encoded with node `agree_dot_example_dot_C` and node `agree_dot_example_dot_A`, respectively. 

Finally, constrained type `top_level_dot_Impl` defined in Line 41 of the IML description is encoded with node `agree_dot_example_dot_top_level_dot_Impl`. 
Besides a contract section, this node contains implementation details on its sub-components and the connections among them. 
Right after its contract section, local variables `A_sub_Output` (Line 38 of the encoding)  and `B_sub_Output` (Line 39 of the encoding) are respectively introduced for the output of `agree_dot_example_dot_A` and `agree_dot_example_dot_B`.
The encoding of node `agree_dot_example_dot_top_level_dot_Impl` is completed by a `let` section (Lines 40 - 45 of the encoding), which lists the assignment constraints reflecting the connections among the subcomponents and optionally annotates the node as top-level. 