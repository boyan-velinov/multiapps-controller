package org.cloudfoundry.multiapps.controller.core.helpers;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.cloudfoundry.multiapps.mta.helpers.SimplePropertyVisitor;
import org.cloudfoundry.multiapps.mta.resolvers.Reference;
import org.cloudfoundry.multiapps.mta.resolvers.ReferencePattern;

public abstract class ReferencingPropertiesVisitor implements SimplePropertyVisitor {

    private final ReferencePattern referencePattern;
    private final Predicate<Reference> relevantReferencesFilter;

    protected ReferencingPropertiesVisitor(ReferencePattern referencePattern) {
        this(referencePattern, null);
    }

    protected ReferencingPropertiesVisitor(ReferencePattern referencePattern, Predicate<Reference> relevantReferencesFilter) {
        this.referencePattern = referencePattern;
        this.relevantReferencesFilter = relevantReferencesFilter;
    }

    protected abstract Object visit(String key, String value, List<Reference> references);

    private List<Reference> removeIrrelevant(List<Reference> references) {
        if (relevantReferencesFilter != null) {
            return references.stream()
                             .filter(relevantReferencesFilter)
                             .collect(Collectors.toList());
        }
        return references;
    }

    @Override
    public Object visit(String key, String value) {
        List<Reference> references = removeIrrelevant(referencePattern.match(value));
        if (!references.isEmpty()) {
            return visit(key, value, references);
        }
        return value;
    }

}
