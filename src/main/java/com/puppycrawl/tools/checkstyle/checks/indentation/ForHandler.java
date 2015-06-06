////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Handler for for loops.
 *
 * @author jrichard
 */
public class ForHandler extends BlockParentHandler {
    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public ForHandler(IndentationCheck indentCheck,
        DetailAST ast, ExpressionHandler parent) {
        super(indentCheck, "for", ast, parent);
    }

    /**
     * Check the indentation of the parameters of the 'for' loop.
     */
    private void checkForParams() {
        final IndentLevel expected =
            new IndentLevel(getLevel(), getBasicOffset());
        final DetailAST init = getMainAst().findFirstToken(TokenTypes.FOR_INIT);

        if (init != null) {
            checkExpressionSubtree(init, expected, false, false);

            final DetailAST cond =
                getMainAst().findFirstToken(TokenTypes.FOR_CONDITION);
            checkExpressionSubtree(cond, expected, false, false);

            final DetailAST iter =
                getMainAst().findFirstToken(TokenTypes.FOR_ITERATOR);
            checkExpressionSubtree(iter, expected, false, false);
        }
        // for each
        else {
            final DetailAST forEach =
                getMainAst().findFirstToken(TokenTypes.FOR_EACH_CLAUSE);
            checkExpressionSubtree(forEach, expected, false, false);
        }
    }

    @Override
    public void checkIndentation() {
        checkForParams();
        super.checkIndentation();
        final LineWrappingHandler lineWrap =
            new LineWrappingHandler(getIndentCheck(), getMainAst(),
                getForLoopRightParen(getMainAst()));
        lineWrap.checkIndentation();
    }

    @Override
    public IndentLevel suggestedChildLevel(ExpressionHandler child) {
        if (child instanceof ElseHandler) {
            return getLevel();
        }
        return super.suggestedChildLevel(child);
    }

    /**
     * Returns right parenthesis of for-loop statement.
     * @param literalForAst
     *          literal-for ast node(TokenTypes.LITERAL_FOR)
     * @return right parenthesis of for-loop statement.
     */
    private static DetailAST getForLoopRightParen(DetailAST literalForAst) {
        return literalForAst.findFirstToken(TokenTypes.RPAREN);
    }
}
