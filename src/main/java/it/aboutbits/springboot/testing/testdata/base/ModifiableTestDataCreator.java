package it.aboutbits.springboot.testing.testdata.base;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.UnaryOperator;

@SuppressWarnings({"java:S119"})
@Slf4j
@NullMarked
public abstract class ModifiableTestDataCreator<CREATOR extends ModifiableTestDataCreator<CREATOR, ITEM, PARAMETER>, ITEM, PARAMETER> extends TestDataCreator<ITEM> {
    @SuppressWarnings("unused")
    protected static final FakerExtended FAKER = new FakerExtended();

    private boolean mutatorSet = false;
    private boolean mutatorCalled = false;

    protected BiFunction<PARAMETER, Integer, PARAMETER> parameterMutator = (parameter, _) -> {
        mutatorCalled = true;
        return parameter;
    };

    protected ObjIntConsumer<ITEM> resultMutator = (_, _) -> {
    };

    protected ModifiableTestDataCreator(int count) {
        super(count);
    }

    @SuppressWarnings({"unchecked", "unused"})
    public CREATOR modifyParameter(BiFunction<PARAMETER, Integer, PARAMETER> parameterMutator) {
        this.parameterMutator = (parameter, index) -> {
            mutatorCalled = true;
            return parameterMutator.apply(parameter, index);
        };
        mutatorSet = true;
        return (CREATOR) this;
    }

    @SuppressWarnings({"unchecked", "unused"})
    public CREATOR modifyParameter(UnaryOperator<PARAMETER> parameterMutator) {
        this.parameterMutator = (parameter, index) -> {
            mutatorCalled = true;
            return parameterMutator.apply(parameter);
        };
        mutatorSet = true;
        return (CREATOR) this;
    }

    @SuppressWarnings({"unchecked", "unused"})
    public CREATOR modifyResult(ObjIntConsumer<ITEM> resultMutator) {
        this.resultMutator = resultMutator;
        return (CREATOR) this;
    }

    @SuppressWarnings({"unchecked", "unused"})
    public CREATOR modifyResult(Consumer<ITEM> resultMutator) {
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

    protected abstract ITEM saveMutation(ITEM item);
}
