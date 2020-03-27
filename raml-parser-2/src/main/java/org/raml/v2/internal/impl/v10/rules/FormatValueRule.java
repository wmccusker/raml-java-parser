/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.raml.v2.internal.impl.v10.rules;

import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.nodes.FloatingNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NullNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class FormatValueRule extends Rule
{
    private String format;
    private final boolean nillable;

    public FormatValueRule(String format)
    {
        this(format, false);
    }

    public FormatValueRule(String format, boolean nillable)
    {
        this.format = format;
        this.nillable = nillable;
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        if (node instanceof FloatingNode && (format.equals("long") || format.startsWith("int")))
        {
            FloatingNode floatingNode = (FloatingNode) node;
            BigDecimal value = floatingNode.getValue();
            BigDecimal roundedValue = floatingNode.getValue().setScale(0, RoundingMode.DOWN);
            return roundedValue.compareTo(value) == 0;
        }

        return true;
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (matches(node))
        {
            if ( node instanceof NullNode && nillable) {
                return createNodeUsingFactory(node);
            } else {
                return createNodeUsingFactory(node, ((SimpleTypeNode) node).getValue());
            }        }
        else
        {
            return RamlErrorNodeFactory.createInvalidFormatValue(
                    ((SimpleTypeNode) node).getValue().toString(),
                    format);
        }
    }

    @Override
    public String getDescription()
    {
        return "Format of value";
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        return Collections.emptyList();
    }
}
