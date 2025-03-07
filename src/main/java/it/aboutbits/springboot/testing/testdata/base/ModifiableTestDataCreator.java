package it.aboutbits.springboot.testing.testdata.base;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.UnaryOperator;

@SuppressWarnings("unchecked")
@Slf4j
public abstract class ModifiableTestDataCreator<CREATOR extends ModifiableTestDataCreator<CREATOR, ITEM, PARAMETER>, ITEM, PARAMETER> extends TestDataCreator<ITEM> {
    protected static final FakerExtended FAKER = new FakerExtended();

    private boolean mutatorSet = false;
    private boolean mutatorCalled = false;

    protected BiFunction<PARAMETER, Integer, PARAMETER> parameterMutator = (parameter, index) -> {
        mutatorCalled = true;
        return parameter;
    };
    protected ObjIntConsumer<ITEM> resultMutator = (item, index) -> {
    };

    protected ModifiableTestDataCreator(int count) {
        super(count);
    }

    public CREATOR modifyParameter(@NonNull BiFunction<PARAMETER, Integer, PARAMETER> parameterMutator) {
        this.parameterMutator = (parameter, index) -> {
            mutatorCalled = true;
            return parameterMutator.apply(parameter, index);
        };
        mutatorSet = true;
        return (CREATOR) this;
    }

    public CREATOR modifyParameter(@NonNull UnaryOperator<PARAMETER> parameterMutator) {
        this.parameterMutator = (parameter, index) -> {
            mutatorCalled = true;
            return parameterMutator.apply(parameter);
        };
        mutatorSet = true;
        return (CREATOR) this;
    }

    public CREATOR modifyResult(@NonNull ObjIntConsumer<ITEM> resultMutator) {
        this.resultMutator = resultMutator;
        return (CREATOR) this;
    }

    public CREATOR modifyResult(@NonNull Consumer<ITEM> resultMutator) {
        this.resultMutator = (item, index) -> resultMutator.accept(item);
        return (CREATOR) this;
    }

    @Override
    protected List<ITEM> create() {
        var result = new ArrayList<ITEM>();

        for (var index = 0; index < numberOfItems; index++) {
            var item = create(index);

            resultMutator.accept(item, index);

            result.add(
                    saveMutation(item)
            );
        }

        if (mutatorSet && !mutatorCalled) {
            log.error("Parameter-mutation is defined but was never called.");
            throw new IllegalStateException("Parameter-mutation is defined but was never called.");
        }

        return result;
    }

    protected abstract ITEM saveMutation(@NonNull ITEM item);
}
